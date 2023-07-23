<template>

  <v-sheet>
    <h1 class="text-3xl font-semibold text-center text-blue-700 mt-5 mb-5">Tantárgyaim</h1>
    <div class="pb-3 m-3 flex justify-between content-center flex-wrap">
      <div>
        <v-btn
            prepend-icon="mdi-plus"
            color="success"
            @click="newSubject">
          Új tantárgy
        </v-btn>
      </div>
      <div>
        <v-switch
            label="Archívált kurzusok megjelenítése"
            v-model="showArchived"
            color="indigo-darken-3"
        ></v-switch>
      </div>
    </div>

    <div v-if="subjects && subjects.length === 0">
      <v-alert
          type="info"
          title="Még nem hozott létre tantárgyat"
      >
      </v-alert>
    </div>
    <div class="tile-grid">
      <div class="m-3" v-for="subject in subjects" :key="subject.id">
        <subject-tile :subject="subject"></subject-tile>
      </div>
    </div>

    <div class="text-center py-5">
      <v-pagination
          v-model="page"
          :length="pageNumber"
      ></v-pagination>
    </div>
  </v-sheet>


</template>

<script>
import SubjectTile from "@/components/SubjectTile.vue";

export default {
  name: "HomeView",
  components: {
    SubjectTile
  },
  methods: {
    newSubject() {
      this.$router.push(`/teacher-subject-detail`)
    },
    loadData() {
      this.$rest.getSubjectForTeacher(
          {
            params: {
              archive: this.showArchived,
              page: this.page - 1
            }
          },
          (response) => {
            this.pageNumber = response.data.totalPages
            this.subjects = response.data.content
          }
      )
    }
  },
  mounted() {
    this.loadData()
  },
  watch: {
    showArchived: function () {
      this.loadData()
    },
    page: function () {
      this.loadData()
    }
  },
  data() {
    return {
      showArchived: false,
      page: 1,
      pageNumber: 1,
      //TODO occup, rating
      subjects: []
    }

  }
}
</script>

<style scoped>

.tile-grid {
  @apply grid min-[900px]:grid-cols-2 min-[1200px]:grid-cols-2 min-[1300px]:grid-cols-3 min-[1930px]:grid-cols-4
}

</style>