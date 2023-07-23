<template>
  <h1 class="text-3xl font-semibold text-center text-blue-700 mt-5 mb-5">Felvehető tantárgyak</h1>

  <div v-if="subjects && subjects.length === 0">
    <v-alert
        type="info"
        title="Jelenleg nincs új elérhető tantárgy"
    >
    </v-alert>
  </div>

  <div class="tile-grid">
    <div class="m-3" v-for="subject in subjects" :key="subject.id">
      <student-subject-tile
          :check-enable="false"
          :subject="subject"
          route-url="enroll-subject"
      ></student-subject-tile>
    </div>
  </div>
</template>

<script>
import StudentSubjectTile from "@/components/StudentSubjectTile.vue";

export default {
  name: "SubjectsView",
  components: {StudentSubjectTile},
  data() {
    return {
      subjects: []
    }
  },
  methods: {
    loadData() {
      this.$rest.loadEnrollableSubjects((response) => {
        this.subjects = response.data
      })
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