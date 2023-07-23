<template>
  <div class="w-full">
    <video-player
        :autoplay="autoplay"
        controls="true"
        type="video/*"
        :responsive="true"
        :fluid="true"
        :src="getVideoUrl()"
        @play="onPlay"
        @seeked="onSeeked"
        @pause="onPause"
        @ended="onEnded"
        @mounted="playerMounted"
        ref="player"
    />
  </div>
</template>

<script>
import {VideoPlayer} from '@videojs-player/vue'
import 'video.js/dist/video-js.css'
import axios from "axios";


export default {
  name: "VueVideoPlayer",
  props: ['video', 'autoplay'],
  components: {
    VideoPlayer
  },
  data() {
    return {
      player: null
    }
  },
  methods: {
    getVideoUrl() {
      return `${axios.defaults.baseURL}/video/resource/play/${this.video.id}/${this.video.fileName}`
    },

    playerMounted(obj) {
      this.player = obj.player
      const elapsed = localStorage.getItem(`${this.video.id}-elapsed`)
      if (elapsed) {
        this.player.currentTime(elapsed)
      }
    },
    onPlay() {

    },
    onPause() {
      localStorage.setItem(`${this.video.id}-elapsed`, this.player.currentTime())
    },
    onSeeked() {
      localStorage.setItem(`${this.video.id}-elapsed`, this.player.currentTime())
    },
    onEnded() {
      localStorage.removeItem(`${this.video.id}-elapsed`)
      //TODO mark on server as finished
    }
  },
  mounted() {

  }
}
</script>

<style scoped>

</style>