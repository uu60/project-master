// const { defineConfig } = require('@vue/cli-service')
// const {proxy} = require("vue/src/core/instance/state");
// module.exports = defineConfig({
//   transpileDependencies: true
// })

module.exports = {
    pages: {
        index: {
            entry: 'src/main.js',
        },
    },
    devServer: {
        proxy: {
            '/stock': {
                target: 'http://8.137.96.5:80',
                secure: false,
                pathRewrite: {'^/stock': ''},
                ws: true,
                changeOrigin: true
            },

            '/registe': {
                target: 'http://8.137.96.5:80',
                secure: false,
                pathRewrite: {'^/registe': ''},
                ws: true,
                changeOrigin: true
            },
            '/denglu': {
                target: 'http://8.137.96.5:80',
                secure: false,
                pathRewrite: {'^/denglu': ''},
                ws: true,
                changeOrigin: true
            },
            '/collectionList': {
                target: 'http://8.137.96.5:80',
                secure: false,
                pathRewrite: {'^/collectionList': ''},
                ws: true,
                changeOrigin: true
            },
            '/deleteItem': {
                target: 'http://8.137.96.5:80',
                secure: false,
                pathRewrite: {'^/deleteItem': ''},
                ws: true,
                changeOrigin: true
            },
            '/collectionCheck': {
                target: 'http://8.137.96.5:80',
                secure: false,
                pathRewrite: {'^/collectionCheck': ''},
                ws: true,
                changeOrigin: true
            }
        }
    },

    lintOnSave: false,

}
