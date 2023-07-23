<template>
  <div class="flex justify-center">
    <h1 class="page-title" v-if="video"> {{ video.title }}</h1>
  </div>
  <div class="flex justify-center my-5" v-if="video">
    <div class="w-3/4">
      <vue-video-player :video="video" :autoplay="false"></vue-video-player>
    </div>
  </div>

  <div class="flex justify-center my-5 gap-5"
       v-if="video && !video.archived">
    <v-btn
        icon="mdi-archive"
        color="error"
        size="large"
        @click="confirmArchiveVideo()"
        v-if="this.video && this.video.createdBy===this.userId"
    >
    </v-btn>
  </div>

  <confirm-dialog
      :model="archiveConfirmDialog"
      title="Videó archiválása"
      message="Biztosan archiválja? Az aktív órákhoz kötött videó a hallgatók számára továbbra is látható marad."
      success-button="Rendben"
      success-icon="mdi-archive"
      :success-callback="archiveVideo"
      :close-confirm-callback="closeConfirm"
  ></confirm-dialog>

</template>

<script>

import {useRoute} from "vue-router";
import VueVideoPlayer from "@/components/VueVideoPlayer.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";

export default {
  name: "VideoDetailView",
  components: {ConfirmDialog, VueVideoPlayer},
  data() {
    return {
      id: null,
      video: null,
      userId: null,
      archiveConfirmDialog: false,
    }
  },
  methods: {
    closeConfirm() {
      this.archiveConfirmDialog = false
    },
    confirmArchiveVideo() {
      this.archiveConfirmDialog = true
    },
    archiveVideo() {
      this.$rest.archiveVideo(this.id, () => {
        this.archiveConfirmDialog = false
        this.$emit("actionSuccess")
        this.$router.push(`/video`)
      })

    },
    loadVideo() {
      this.$rest.loadVideoData(this.id, (response) => {
        this.video = response.data
      })
    }
  },
  mounted() {
    const route = useRoute()
    this.id = route.params.id
    this.loadVideo()
  },
  created() {
    this.userId = localStorage.getItem("userId")
  }
}
</script>

<style scoped>


</style>