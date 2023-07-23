<template>
  <h1 class="text-3xl font-semibold text-center text-blue-700 mt-5 mb-5">Tantárgyaim</h1>

  <v-tabs
      v-model="tab"
      bg-color="primary"

  >
    <v-tab value="active">Aktív tantárgyak</v-tab>
    <v-tab value="finished">Teljesített tantárgyak</v-tab>
  </v-tabs>

  <v-window v-model="tab">
    <v-window-item value="active">

      <v-alert
          v-if="activeSubjects.length === 0"
          type="info"
          title="Nem található felvett tantárgy"
          text="A tantárgyak menü pont alatt található tárgyakból tud választani."
          class="mt-5"
      >
      </v-alert>

      <div class="tile-grid">
        <div class="m-3" v-for="subject in activeSubjects" :key="subject.id">
          <student-subject-tile
              :check-enable="true"
              :subject="subject"
              route-url="student-subject-detail"
          ></student-subject-tile>
        </div>
      </div>

    </v-window-item>
    <v-window-item value="finished">
      <v-alert
          v-if="finishedSubjects.length === 0"
          type="info"
          title="Még egyetlen tárgyat se végzett el."
          class="mt-5"
      >
      </v-alert>

      <div class="tile-grid">
        <div class="m-3" v-for="subject in finishedSubjects" :key="subject.id">
          <student-subject-tile
              :check-enable="false"
              :subject="subject"
              route-url=""
              :route-disabled="true"
          ></student-subject-tile>
        </div>
      </div>
    </v-window-item>

  </v-window>


</template>

<script>
import StudentSubjectTile from "@/components/StudentSubjectTile.vue";

export default {
  name: "HomeView",
  components: {StudentSubjectTile},

  data() {
    return {
      tab: null,
      activeSubjects: [],
      finishedSubjects: []
    }
  },
  methods: {
    loadData() {
      this.$rest.loadEnrolledForStudent((response) => {
        this.activeSubjects = response.data
      })
      this.$rest.loadFinishedForStudent(
          {
            params: {
              status: 'FINISHED'
            }
          },
          (response) => {
            this.finishedSubjects = response.data
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
  @apply grid min-[900px]:grid-cols-2 min-[1200px]:grid-cols-2 min-[1300px]:grid-cols-3 min-[1930px]:grid-cols-4
}
</style>