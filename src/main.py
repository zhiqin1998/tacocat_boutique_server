import base64
import json
import pickle

import cv2
import numpy
import requests
import werkzeug.exceptions
from PIL import Image
from flask import Flask, abort, jsonify, request

url = "https://fashion.recoqnitics.com/analyze"
accessKey = "9ff85e93750d9449738a"
secretKey = "2bd11d401fec71f18bdd261898e958f49f160f53"
data = {'access_key': accessKey, 'secret_key': secretKey}

app = Flask(__name__)
users = {}


def save():
    pickle.dump(users, 'data/users.pickle')


def load():
    global users
    users = pickle.load('data/users.pickle')


@app.errorhandler(werkzeug.exceptions.BadRequest)
def handle_bad_request(e):
    return 'Bad request!', 400


@app.route('/')
def index():
    return json.dumps(users)


@app.route('/getuser', methods=['GET'])
def get_user():
    user_id = int(request.values['user_id'])
    if user_id not in users:
        abort(404)
    else:
        user = users[user_id]
        return jsonify({'user': user.toJSON()})


@app.route('/getusertop', methods=['GET'])
def get_user_top():
    user_id = int(request.values['user_id'])
    if user_id not in users:
        abort(404)
    else:
        user = users[user_id]
        user.update_top()

        return jsonify({'user_id': user_id, 'top_colors': user.top_colors, 'top_garments': user.top_garment,
                        'top_styles': user.top_styles, 'top_colors_hex': user.top_colors_hex})


@app.route('/newuser', methods=['POST'])
def new_user():
    user_name = request.values['user_name']
    if user_name is None:
        abort(400)
    u = User()
    u.name = user_name
    id = len(users)
    users[id] = u
    return jsonify({'user_id': id, 'user': u.toJSON()})


@app.route('/newphoto', methods=['POST'])
def new_photo():
    user_id = int(request.values['user_id'])
    data = request.files['img_data']
    if user_id is None or data is None:
        abort(400)
    if user_id not in users:
        abort(404)
    res = users[user_id].add_photo(data)
    if not res:
        abort(400)
    return jsonify({'message': 'success'})


class User:
    def __init__(self):
        self.name = None
        self.photos = []
        self.top_colors = []
        self.top_colors_hex = []
        self.top_styles = []
        self.top_garment = []

    def add_photo(self, file):
        filestr = file.read()
        npimg = numpy.fromstring(filestr, numpy.uint8)
        img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        im_pil = Image.fromarray(img)
        im_pil.save('temp.png')
        enc = open('temp.png', 'rb')
        p = Photo()
        p.photo_id = len(self.photos)
        filename = {'filename': enc}
        print('api requesting for {}'.format(self.name))
        r = requests.post(url, files=filename, data=data)
        if not r:
            print(r.text)
            return False
        content = json.loads(r.content)
        print(content)
        with open("temp.png", "rb") as image_file:
            p.photo_data = str(base64.b64encode(image_file.read()))
        for color in content['person']['colors']:
            c = Color()
            c.hex = color['hex']
            c.color_name = color['colorName']
            c.category = color['colorGeneralCategory']
            c.ratio = float(color['ratio'])
            p.colors.append(c)
        p.colors.sort()
        for style in content['person']['styles']:
            s = Style()
            s.name = style['styleName']
            s.confidence = float(style['confidence'])
            p.styles.append(s)
        p.styles.sort()
        for garment in content['person']['garments']:
            g = Garment()
            g.name = garment['typeName']
            g.confidence = float(garment['confidence'])
            g.bounding_box = garment['boundingBox']
            p.garments.append(g)
        p.garments.sort()
        self.photos.append(p)
        return True

    def update_top(self):
        garments_dict = {}
        styles_dict = {}
        colors_dict = {}
        hex_dict = {}
        for photo in self.photos:
            for color in photo.colors:
                name = color.color_name.replace("_", " ").title()
                if name not in colors_dict:
                    colors_dict[name] = color.ratio
                    hex_dict[color.hex] = color.ratio
                else:
                    colors_dict[name] += color.ratio
                    hex_dict[color.hex] += color.ratio
            for garment in photo.garments:
                if garment.name not in garments_dict:
                    garments_dict[garment.name] = garment.confidence
                else:
                    garments_dict[garment.name] += garment.confidence
            for style in photo.styles:
                if style.name not in styles_dict:
                    styles_dict[style.name] = style.confidence
                else:
                    styles_dict[style.name] += style.confidence
        self.top_colors = []
        self.top_garment = []
        self.top_styles = []
        self.top_colors_hex = []
        colortuple = sorted(colors_dict.items(), reverse=True, key=lambda x: x[1])
        for elem in colortuple:
            self.top_colors.append(elem[0])
        garmenttuple = sorted(garments_dict.items(), reverse=True, key=lambda x: x[1])
        for elem in garmenttuple:
            self.top_garment.append(elem[0])
        styletuple = sorted(styles_dict.items(), reverse=True, key=lambda x: x[1])
        for elem in styletuple:
            self.top_styles.append(elem[0])
        hextuple = sorted(hex_dict.items(), reverse=True, key=lambda x: x[1])
        for elem in hextuple:
            self.top_colors_hex.append(elem[0])

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


class Photo:
    def __init__(self):
        self.photo_id = None
        self.photo_data = None
        self.colors = []
        self.styles = []
        self.garments = []

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


class Style:
    def __init__(self):
        self.name = None
        self.confidence = None

    def __lt__(self, other):
        return self.confidence < self.confidence

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


class Garment:
    def __init__(self):
        self.name = None
        self.confidence = None
        self.bounding_box = None

    def __lt__(self, other):
        return self.confidence < self.confidence

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


class Color:
    def __init__(self):
        self.hex = None
        self.color_name = None
        self.category = None
        self.ratio = None

    def __lt__(self, other):
        return self.ratio < other.ratio

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


if __name__ == '__main__':
    app.run(debug=True)
