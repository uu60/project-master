<template>
  <div>
    <iframe src="https://wallstreetcn.com/" id="qt"  style="left: 50px; right:50px; top: 50px; bottom: 50px; width: 1400px; height: 600px"></iframe>
  </div>
</template>


<script>
export default {
  name: "Menu4Page",
  mounted(){
    /**
     * iframe-宽高自适应显示
     */
    // function changeMobsfIframe(){
    //   const qt = document.getElementById('qt')
    //   const deviceWidth = document.body.clientWidth
    //   const deviceHeight = document.body.clientHeight
    //   qt.style.width = Number(deviceWidth) + 'px' // 数字是页面布局宽度差值
    //   qt.style.height = Number(deviceHeight) + 'px' // 数字是页面布局高度差
    // }
    //
    // changeMobsfIframe()
    //
    // window.onresize = function(){
    //   changeMobsfIframe()
    // }
  },
}
</script>

<style scoped lang="less">
iframe.noScrolling{
  width: 1080px; /*or any other size*/
  height: 960px; /*or any other size*/
  overflow: hidden;
}
</style>