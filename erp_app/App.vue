<script>
import store from './utils/store.js'

export default {
  onLaunch: function() {
    console.log('App Launch')
    // 初始化store
    store.init()
  },
  onShow: function() {

    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style>
/*每个页面公共css */
</style>
