<template>
  <v-card variant="outlined"
          @click="routeToDetail(video.id)"
          class="video-tile flex"
          :class="{archived: video.archived}"
  >

    <div class="w-full flex-col">
      <div class="video-title text-center w-full">
        {{ video.title }}
      </div>
      <div class="py-5 w-full">
        <img class="video-tile-background" v-if="video.thumbnail" :src="getImage()" alt="Thumbnail">
        <img src="@/assets/video-placeholder.png" class="video-tile-background" v-if="!video.thumbnail">
      </div>


      <div class="flex justify-end mx-5 w-full">
        <v-icon v-if="video.archived" icon="mdi-archive" size="large"></v-icon>
        <v-icon v-if="video.public" icon="mdi-earth" size="large"></v-icon>
      </div>
    </div>

  </v-card>
</template>

<script>

import axios from "axios";

export default {
  name: "VideoTile",
  props: ['video'],
  methods: {
    routeToDetail(id) {
      this.$router.push(`/video-detail/${id}`)
    },
    getImage() {
      return `${axios.defaults.baseURL}/video/resource/image/${this.video.thumbnail}`
    }
  },
  created() {
    this.getImage()
  }
}
</script>

<style scoped>
.video-title {
  @apply text-blue-600 font-bold text-2xl mt-3 mb-1
}

.video-tile {
  @apply p-1 min-[900px]:min-h-[36vh] min-[1200px]:min-h-[32vh] min-[1300px]:min-h-[34vh] min-[1930px]:min-h-[32vh]
}

.archived {
  @apply bg-gray-100
}

.video-tile-background {
  width: 100%;
  height: 20vh;
  object-fit: cover;
}
</style>