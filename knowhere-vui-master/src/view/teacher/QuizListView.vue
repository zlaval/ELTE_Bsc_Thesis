<template>

  <div class="flex justify-center">
    <h1 class="page-title">Kvízek</h1>
  </div>
  <div>
    <v-btn
        class="mx-3 mt-3"
        prepend-icon="mdi-plus"
        color="success"
        @click="newQuiz">
      Új Kvíz
    </v-btn>
  </div>

  <div v-if="quizzes.length === 0" class="mt-5">
    <v-alert
        type="info"
        title="Még nem hozott létre kérdéssort."
    >
    </v-alert>
  </div>

  <div class="tile-grid mt-5">
    <div class="m-3" v-for="quiz in quizzes" :key="quiz.id">
      <quiz-tile :quiz="quiz" @archive="confirmArchiveQuiz"></quiz-tile>
    </div>
  </div>


  <confirm-dialog
      :model="archiveConfirmDialog"
      title="Kvíz archiválása"
      message="Biztosan archiválja? Az aktív órákhoz kötött kvíz a hallgatók számára továbbra is látható marad."
      success-button="Rendben"
      success-icon="mdi-archive"
      :success-callback="archiveQuiz"
      :close-confirm-callback="closeConfirm"
  ></confirm-dialog>
</template>

<script>
import QuizTile from "@/components/QuizTile.vue";
import ConfirmDialog from "@/components/ConfirmDialog.vue";

export default {
  name: "QuizListView",
  components: {ConfirmDialog, QuizTile},
  data() {
    return {
      quizzes: [],
      archiveConfirmDialog: false,
      archiveId: null
    }
  },
  methods: {
    newQuiz() {
      this.$router.push(`/quiz-editor`)
    },
    editQuiz(id) {
      this.$router.push(`/quiz-editor/${id}`)
    },
    confirmArchiveQuiz(id) {
      this.archiveConfirmDialog = true
      this.archiveId = id
    },
    closeConfirm() {
      this.archiveConfirmDialog = false
    },
    archiveQuiz() {
      this.$rest.archiveQuiz(this.archiveId, () => {
        this.archiveId = null
        this.archiveConfirmDialog = false
        this.loadData()
        this.$emit("actionSuccess")
      })
    },
    loadData() {
      this.$rest.loadQuizzes((response) => {
        this.quizzes = response.data
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
  @apply grid min-[900px]:grid-cols-2 min-[1200px]:grid-cols-3 min-[1300px]:grid-cols-4 min-[1930px]:grid-cols-5
}

</style>