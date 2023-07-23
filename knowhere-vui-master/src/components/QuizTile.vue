<template>
  <v-card variant="outlined" class="quiz-tile flex">

    <v-card-item class="h-full w-full">
      <div class="quiz-title">
        {{ quiz.name }}
      </div>
      <div
          class="text-caption mt-2 h-30 min-[600px]:h-24 min-[600px]:h-20 min-[900px]:h-24 min-[900px]:h-26 overflow-hidden w-full">
        {{ getCroppedText(quiz.description) }}
      </div>

      <div class="flex justify-between flex-wrap">
        <v-btn
            icon="mdi-archive"
            color="error"
            variant="tonal"
            size="small"
            class="mt-2"
            @click="archiveQuiz"
        ></v-btn>
        <v-btn
            icon="mdi-pencil"
            color="success"
            variant="tonal"
            size="small"
            class="mt-2"
            @click="editQuiz(quiz.id)"
        ></v-btn>
      </div>


    </v-card-item>


  </v-card>
</template>

<script>
import {getCroppedText} from "../util/utils";

export default {
  name: "QuizTile",
  props: ['quiz'],
  methods: {
    getCroppedText,
    archiveQuiz() {
      this.$emit("archive", this.quiz.id)
    },
    editQuiz(id) {
      this.$router.push(`/quiz-editor/${id}`)
    },
  }
}
</script>

<style scoped>
.quiz-title {
  @apply text-blue-600 font-bold text-lg mt-3 mb-1 justify-center flex
}

.quiz-tile {
  @apply p-1
}

</style>