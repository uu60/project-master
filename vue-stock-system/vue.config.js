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

    lintOnSave: false,

//开启代理服务器
    devServer: {
        //proxy: 'http://localhost:5000'
    },
}
