<template>
  <v-text-field
      v-model="internalDt"
      :key="internalDt"
      :label="label"
      type="datetime-local"
      @input="handleInput"
      :rules="getRules()"
  >
  </v-text-field>
</template>

<script>
import {formatToHtmlDateTime, formatToIso8601Date, ValidationRules} from "@/util/utils";

export default {
  name: "Iso8601DateTimePicker",
  props: {
    modelValue: String,
    label: String,
    required: Boolean
  },
  methods: {
    getRules() {
      if (this.required) {
        return ValidationRules.requiredRule
      } else
        return []
    },
    handleInput() {
      this.$emit('update:modelValue', formatToIso8601Date(this.internalDt))
    }
  },
  data() {
    return {
      internalDt: formatToHtmlDateTime(this.modelValue),
    }
  }

}
</script>

<style scoped>

</style>