class R(dict):
    def __init__(self):
        super().__init__()
        self['code'] = 0
        self['msg'] = None
        self['data'] = None

    @staticmethod
    def ok(msg="success"):
        r = R()
        r['msg'] = msg
        return r

    @staticmethod
    def other(code, msg=None):
        r = R()
        r['code'] = code
        r['msg'] = msg
        return r

    def set_data(self, data):
        self['data'] = data
        return self

    class Codes:
        SUCCESS = 0
        K_DATA_WAIT = 1