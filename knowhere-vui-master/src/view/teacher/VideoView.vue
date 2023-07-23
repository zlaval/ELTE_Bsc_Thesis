<template>
  <h1 class="text-3xl font-semibold text-center text-blue-700 mt-5 mb-5">Videók</h1>

  <v-tabs
      v-model="tab"
      bg-color="primary"

  >
    <v-tab value="own">Saját videók</v-tab>
    <v-tab value="public">Publikus videók</v-tab>
  </v-tabs>

  <v-window v-model="tab">
    <v-window-item value="own">
      <v-sheet>

        <div class="pb-3 py-3 m-3 flex justify-between content-center flex-wrap">
          <div>
            <v-btn
                prepend-icon="mdi-plus"
                color="success"
                @click="newVideo">
              Új videó
            </v-btn>
          </div>
          <div>
            <v-switch
                label="Archívált videók megjelenítése"
                v-model="showArchived"
                color="indigo-darken-3"
            ></v-switch>
          </div>
        </div>

        <div v-if="videos.length === 0">
          <v-alert
              type="info"
              title="Még nem töltött fel videót"
          >
          </v-alert>
        </div>

        <div class="tile-grid">
          <div class="m-3" v-for="video in videos" :key="video.id">
            <video-tile :video="video"></video-tile>
          </div>
        </div>

        <div class="text-center py-5">
          <v-pagination
              v-model="page"
              :length="pageNumber"
          ></v-pagination>
        </div>

      </v-sheet>
    </v-window-item>

    <v-window-item value="public">
      <v-sheet>

        <div v-if="publicVideos.length === 0" class="mt-5">
          <v-alert
              type="info"
              title="Nem található publikusan elérhető videó"
          >
          </v-alert>
        </div>

        <div class="tile-grid">
          <div class="m-3" v-for="video in publicVideos" :key="video.id">
            <video-tile :video="video"></video-tile>
          </div>
        </div>

        <div class="text-center py-5">
          <v-pagination
              v-model="publicPage"
              :length="publicPageNumber"
          ></v-pagination>
        </div>

      </v-sheet>
    </v-window-item>
  </v-window>

</template>

<script>
import VideoTile from "@/components/VideoTile.vue";

export default {
  name: "VideoView",
  components: {VideoTile},
  data() {
    return {
      showArchived: false,
      page: 1,
      pageNumber: 1,
      tab: null,
      videos: [],
      publicVideos: [],
      publicPageNumber: 1,
      publicPage: 1
    }
  },
  watch: {
    showArchived: function () {
      this.loadData()
    },
    page: function () {
      this.loadData()
    },
    publicPage: function () {
      this.loadPublic()
    },
    tab: function () {
      if (this.tab === "public") {
        this.loadPublic()
      }
    }
  },
  methods: {
    newVideo() {
      this.$router.push(`/video-editor`)
    },
    loadPublic() {
      this.$rest.loadPublicVideos(
          {
            params: {
              page: this.publicPage - 1
            }
          },
          (response) => {
            this.publicPageNumber = response.data.totalPages
            this.publicVideos = response.data.content
          }
      )
    },
    loadData() {
      this.$rest.loadVideos(
          {
            params: {
              archive: this.showArchived,
              page: this.page - 1
            }
          },
          (response) => {
            this.pageNumber = response.data.totalPages
            this.videos = response.data.content
          }
      )
    }
  },
  mounted() {
    this.loadData()
  }
}
</script>

<style scoped>
.tile-grid {
  @apply grid min-[900px]:grid-cols-2 min-[1200px]:grid-cols-2 min-[1300px]:grid-cols-4 min-[1930px]:grid-cols-5
}
</style>