module.exports = {
    pages: {
        index: {
            entry: 'src/main.js',
        },
    },
    devServer: {
        proxy: {
            '/api': {
                target: 'http://8.137.96.5',
            },
            '/theDeal':{
                target: 'https://www.thedeal.com/news-and-media/'
            }
        }
    },
    lintOnSave: false,

}
