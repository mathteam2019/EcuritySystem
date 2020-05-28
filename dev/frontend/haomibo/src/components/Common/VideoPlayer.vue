<template>
<div data-vjs-player>
    <video ref="myPlayer" :poster="options.poster" :class="`video-js vjs-default-skin ${options.class}`"/>
</div>
</template>

<script>
import videojs from 'video.js'
import 'video.js/dist/video-js.css'

export default {
    props: ['options'],
    data() {
        return {
            player: ''
        }
    },
    mounted() {
        this.player = videojs(this.$refs.myPlayer, this.options, () => {
        })
    },
    beforeDestroy() {
        if (this.player) {
            this.player.dispose()
        }
    },
    methods: {
      initialize(){
        if (this.player) {
          let selector = '.video-wrapper .video-container .video-js video';
          let els = document.querySelectorAll(selector);
          els[0].src = this.options.sources.src;
        }
      },
      dispose(){
        if (this.player) {
          this.player.pause()
        }
      }
    }
}
</script>
