import base64
import json
import pickle
from io import BytesIO

import cv2
import numpy
import requests
import werkzeug.exceptions
from PIL import Image
from flask import Flask, abort, jsonify, request
from flask.json import JSONEncoder

url = "https://fashion.recoqnitics.com/analyze"
accessKey = "9ff85e93750d9449738a"
secretKey = "2bd11d401fec71f18bdd261898e958f49f160f53"
data = {'access_key': accessKey, 'secret_key': secretKey}


class MyJSONEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, Store):
            return {
                'name': obj.name,
                'location': obj.location,
                'description': obj.description,
                'cloth_list': obj.cloth_list,
            }
        if isinstance(obj, Cloth):
            return {
                'name': obj.name,
                'photo_id': obj.photo_id,
                'photo_data': obj.photo_data,
                'price': obj.price,
                'colors': obj.colors,
                'styles': obj.styles,
                'garments': obj.garments,
            }
        if isinstance(obj, User):
            return {
                'name': obj.name,
                'photos': obj.photos,
                'top_colors': obj.top_colors,
                'top_colors_hex': obj.top_colors_hex,
                'top_styles': obj.top_styles,
                'top_garment': obj.top_garment,
                'wishlist': obj.wishlist,
            }
        if isinstance(obj, Photo):
            return {
                'photo_id': obj.photo_id,
                'photo_data': obj.photo_data,
                'colors': obj.colors,
                'styles': obj.styles,
                'garments': obj.garments,
            }
        if isinstance(obj, Color):
            return {
                'hex': obj.hex,
                'color_name': obj.color_name,
                'category': obj.category,
                'ratio': obj.ratio,
            }
        if isinstance(obj, Garment):
            return {
                'name': obj.name,
                'confidence': obj.confidence,
                'bounding_box': obj.bounding_box,
            }
        if isinstance(obj, Style):
            return {
                'name': obj.name,
                'confidence': obj.confidence,
            }
        return super(MyJSONEncoder, self).default(obj)


app = Flask(__name__)
app.json_encoder = MyJSONEncoder
users = {}
stores = {}


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
        return jsonify(user)


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
    return jsonify({'user_id': id, 'user': u})


@app.route('/newstore', methods=['POST'])
def new_store():
    store_name = request.values['name']
    store_location = request.values['location']
    store_desc = request.values['description']
    if store_name is None:
        abort(400)
    s = Store()
    s.name = store_name
    s.location = store_location
    s.description = store_desc
    id = len(stores)
    stores[id] = s
    return jsonify({'store_id': id, 'store': s})


@app.route('/addcloth', methods=['POST'])
def add_cloth():
    store_id = int(request.values['store_id'])
    price = float(request.values['price'])
    name = request.values['name']
    data = request.files['img_data']
    if store_id not in stores:
        abort(400)
    s = stores[store_id]
    id = len(s.cloth_list)
    p = Cloth()
    p.name = name
    p.store_id = store_id
    p.price = price
    p.photo_id = id
    filestr = data.read()
    npimg = numpy.fromstring(filestr, numpy.uint8)
    img = cv2.imdecode(npimg, cv2.IMREAD_COLOR)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    im_pil = Image.fromarray(img)
    im_pil.save('stemp.png')
    enc = open('stemp.png', 'rb')
    filename = {'filename': enc}
    r = requests.post(url, files=filename, data=data)
    if not r:
        print(r.text)
        return False
    content = json.loads(r.content)
    print(content)
    with open("stemp.png", "rb") as image_file:
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
    return jsonify({'message': 'success'})


@app.route('/newphoto', methods=['POST'])
def new_photo():
    user_id = int(request.values['user_id'])
    data = request.values['img_data']
    if user_id is None or data is None:
        abort(400)
    if user_id not in users:
        abort(404)
    res = users[user_id].add_photo(data)
    if not res:
        abort(400)
    return jsonify({'message': 'success'})


@app.route('/suggestwithphoto', methods=['POST'])
def suggestwithphoto():
    data = request.values['img_data']
    if data is None:
        abort(400)
    p = getphotodetails(data)
    if p is None:
        abort(400)
    stores_list = []
    for s_id in stores:
        s = stores[s_id]
        
    return jsonify({'stores': stores_list})


@app.route('/getphotodetails', methods=['POST'])
def getdetails():
    user_id = int(request.values['user_id'])
    data = request.files['img_data']
    if user_id is None or data is None:
        abort(400)
    if user_id not in users:
        abort(404)
    p = getphotodetails(data, user_id)
    if p is None:
        abort(400)
    return jsonify(p)


def getphotodetails(b64, user_id=None):
    im_pil = Image.open(BytesIO(base64.b64decode(b64)))
    im_pil.save('temp.jpeg', quality=50)
    enc = open('temp.jpeg', 'rb')
    p = Photo()
    filename = {'filename': enc}
    if user_id is not None:
        print('api requesting for {}'.format(users[user_id].name))
    r = requests.post(url, files=filename, data=data)
    if not r:
        print(r.text)
        return None
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
    return p


class User:
    def __init__(self):
        self.name = None
        self.photos = []
        self.wishlist = []
        self.top_colors = []
        self.top_colors_hex = []
        self.top_styles = []
        self.top_garment = []

    def add_photo(self, b64):
        im_pil = Image.open(BytesIO(base64.b64decode(b64)))
        im_pil.save('temp.jpeg', quality=50)
        enc = open('temp.jpeg', 'rb')
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
        with open("temp.jpeg", "rb") as image_file:
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
                    hex_dict[name] = color.hex
                else:
                    colors_dict[name] += color.ratio
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
            self.top_colors_hex.append(hex_dict[elem[0]])
        garmenttuple = sorted(garments_dict.items(), reverse=True, key=lambda x: x[1])
        for elem in garmenttuple:
            self.top_garment.append(elem[0])
        styletuple = sorted(styles_dict.items(), reverse=True, key=lambda x: x[1])
        for elem in styletuple:
            self.top_styles.append(elem[0])


class Photo:
    def __init__(self):
        self.photo_id = None
        self.photo_data = None
        self.colors = []
        self.styles = []
        self.garments = []


class Style:
    def __init__(self):
        self.name = None
        self.confidence = None

    def __lt__(self, other):
        return self.confidence < self.confidence


class Garment:
    def __init__(self):
        self.name = None
        self.confidence = None
        self.bounding_box = None

    def __lt__(self, other):
        return self.confidence < self.confidence


class Color:
    def __init__(self):
        self.hex = None
        self.color_name = None
        self.category = None
        self.ratio = None

    def __lt__(self, other):
        return self.ratio < other.ratio


class Store:
    def __init__(self):
        self.name = None
        self.location = None
        self.description = None
        self.cloth_list = []

    def __lt__(self, other):
        return self.name.__lt__(other.name)


class Cloth(Photo):
    def __init__(self):
        super().__init__()
        self.name = None
        self.store_id = None
        self.price = None


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
