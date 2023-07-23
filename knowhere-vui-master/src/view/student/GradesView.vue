<template>

  <h1 class="page-title">Tanulmányaim</h1>

  <div v-if="gradeData.length === 0" class="mb-5 mt-5">
    <v-alert
        type="info"
        title="Még nem szerzett érdemjegyet"
    >
    </v-alert>
  </div>

  <div v-if="gradeData.length !== 0" class="mt-5">

    <div>
      <v-chip
          class="mt-5 mb-5"
          color="primary"
          variant="outlined"
      >
        Teljesített kredit: {{ sumCredit() }}
      </v-chip>
    </div>

    <v-table
    >
      <thead>
      <tr>
        <th>
          Tantárgy neve
        </th>
        <th>
          Kredit
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
          v-for="grade in gradeData"
          :key="grade.id"
      >
        <td>{{ grade.subject }}</td>
        <td>{{ grade.credit }}</td>
        <td>{{ grade.points }}</td>
        <td>{{ grade.mark }}</td>

      </tr>
      </tbody>

    </v-table>
  </div>

</template>

<script>
export default {
  name: "GradesView",
  data() {
    return {
      gradeData: [],
    }
  },
  methods: {
    loadData() {
      this.$rest.getMyGrades((response) => {
        this.gradeData = response.data
      })
    },
    sumCredit() {
      return this.gradeData.reduce((acc, val) => acc + val.credit, 0)
    }
  },
  mounted() {
    this.loadData()
  }
}
</script>

<style scoped>

</style>