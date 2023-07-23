<template>

  <v-card variant="outlined"
          @click="routeToDetail(subject.id)"
          class="subject-tile flex"
          :disabled="!cardEnabled"
          v-if="subject"
  >
    <v-card-item class="w-full">
      <div class="flex-col w-full">
        <div class="subject-title w-full">
          {{ subject.name }}
        </div>
        <div class="w-full">
          <img class="subject-tile-background" v-if="subject.coverImagePath" :src="getImageSource()">
          <div class="subject-tile-background" v-if="!subject.coverImagePath"></div>
        </div>

        <div class="v-card-subtitle mt-2 w-full">
          {{ getFormattedDate() }}
        </div>
        <div class="text-caption mt-2 h-10 w-full">
          {{ getCroppedText(subject.description) }}
        </div>
        <div class="mt-5 mb-3 flex justify-between align-center w-full">
          <div>
            <v-chip prepend-icon="mdi-credit-card-edit-outline">
              {{ subject.credit }}
            </v-chip>
          </div>
        </div>
      </div>
    </v-card-item>


  </v-card>

</template>

<script>
import axios from "axios";
import {formatDateHu, getCroppedText} from "@/util/utils";

export default {
  name: "StudentSubjectTile",
  props: ['subject', 'checkEnable', 'routeUrl', 'routeDisabled'],
  data() {
    return {
      cardEnabled: false,
    }
  },
  mounted() {
    if (this.checkEnable === true) {
      this.cardEnabled = this.isCardEnabled()
    } else {
      this.cardEnabled = true
    }
  },
  methods: {
    getCroppedText,
    getFormattedDate() {
      const start = formatDateHu(this.subject.startDt)
      let end = 'Korlátlan'
      if (this.subject.endDt) {
        end = formatDateHu(this.subject.endDt)
      }
      return `${start} - ${end}`
    },
    getImageSource() {
      return `${axios.defaults.baseURL}/subject/resource/image/${this.subject.coverImagePath}`
    },
    isCardEnabled() {
      const start = new Date(this.subject.startDt)
      const now = new Date()
      let isEnd = false
      if (this.subject.endDt) {
        const end = new Date(this.subject.endDt)
        isEnd = end <= now
      }
      return (start <= now) && !isEnd
    },
    routeToDetail(id) {
      if (!this.routeDisabled) {
        if (this.cardEnabled) {
          this.$router.push(`/${this.routeUrl}/${id}`)
        }
      }
    }
  }
}
</script>

<style scoped>
.subject-title {
  @apply text-blue-600 font-bold text-xl mt-3 mb-5 flex justify-center
}

.subject-tile {
  @apply p-1 min-h-[30vh]
}

.subject-tile-background {
  width: 100%;
  height: 10vh;
  object-fit: cover;
}

</style>