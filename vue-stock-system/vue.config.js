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
            }
        }
    },
    lintOnSave: false,

}
