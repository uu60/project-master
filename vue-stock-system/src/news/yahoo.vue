<template>
  <div style="height:866px;">
    <!--style="left: 50px; right:50px; top: 50px; bottom: 50px; width: 1400px; height: 600px"-->
    <iframe src="https://www.marketwatch.com/" id="qt"></iframe>
  </div>
</template>


<script>
export default {
  name: "yahoo",
  data() {
    return {
      activeIndex: ''
    }
  },
  mounted() {
    /**
     * iframe-宽高自适应显示
     */
    function changeMobsfIframe() {
      const qt = document.getElementById('qt')
      const deviceWidth = document.body.clientWidth
      const deviceHeight = document.body.clientHeight
      console.log("dw", deviceWidth)
      console.log("dh", deviceHeight)

      qt.style.width = (Number(deviceWidth)-40) + 'px' // 数字是页面布局宽度差值
      qt.style.height = (Number(deviceHeight)-100) + 'px' // 数字是页面布局高度差
      console.log("qw", qt.style.width)
      console.log("qh", qt.style.height)
    }

    changeMobsfIframe()

    window.onresize = function () {
      changeMobsfIframe()
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
      this.$router.push(key);
    },
  }
}
</script>

<style scoped lang="less">
iframe.noScrolling {
  width: 1080px; /*or any other size*/
  height: 960px; /*or any other size*/
  overflow: hidden;
}
</style>