<template>

  <div v-if="!subjectId" class="mb-5 mt-3">
    <v-alert
        type="info"
        title="Tantárgyon szerzett érdemjegyek"
        text=" Kérem válassza ki a megtekinteni kívánt tantárgyat"
    >
    </v-alert>

  </div>

  <v-select
      v-model="subjectId"
      :items="subjects"
      item-title="name"
      item-value="id"
      label="Tantárgy"
  ></v-select>

  <div v-if="subjectData">
    <div class="flex justify-center">
      <h1 class="page-title">{{ subjectData.name }}</h1>
    </div>


    <v-table
        v-if="subjectData.grades.length!==0"
    >
      <thead>
      <tr>
        <th>
          Tanuló neve
        </th>
        <th>
          Azonosító
        </th>
        <th>
          Összpontszám
        </th>
        <th>
          Érdemjegy
        </th>
      </tr>
      </thead>
      <tbody>
      <tr
          v-for="grade in subjectData.grades"
          :key="grade.email"
      >
        <td>{{ grade.studentName }}</td>
        <td>{{ grade.email }}</td>
        <td>{{ grade.points }}</td>
        <td>{{ grade.mark }}</td>

      </tr>
      </tbody>

    </v-table>

    <v-alert
        v-if="subjectData.grades.length===0"
        type="warning"
        title="Nem találhatók érdemjegyek"
        text="Úgy tűnik, a tantárgyat még senki nem teljesítette."
    >
    </v-alert>

  </div>
  <div v-if="subjectId && !subjectData">
    <v-alert
        type="error"
        title="Nem található a keresett tantárgy"
        text="Úgy tűnik, a rendszer nem tudja kiszolgálni a kérést. Kérjük próbálja meg újra később."
    >
    </v-alert>
  </div>


</template>

<script>
export default {
  name: "StudentGrades",
  data() {
    return {
      subjectId: null,
      subjects: [],
      subjectData: null
    }
  },
  watch: {
    subjectId: function () {
      this.loadData()
    }
  },
  methods: {
    loadSelectData() {
      this.$rest.getSubjectsForSelect((response) => {
        this.subjects = response.data
      })
    },
    loadData() {
      if (this.subjectId) {
        this.$rest.getSubjectWithGrade(this.subjectId, (response) => {
          this.subjectData = response.data
        })
      } else {
        this.subjectData = null
      }
    }
  },
  mounted() {
    this.loadSelectData()
  }
}
</script>

<style scoped>

</style>