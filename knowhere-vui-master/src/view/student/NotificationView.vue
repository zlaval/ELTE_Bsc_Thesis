<template>
  <h1 class="page-title">Üzenetek</h1>

  <div v-if="notifications.length === 0" class="mb-5 mt-5">
    <v-alert
        type="info"
        title="Nincsenek értesítések"
    >
    </v-alert>
  </div>

  <div class="flex justify-center">
    <v-list

    >
      <v-list-item
          v-for="notification in notifications"
          :key="notification.id"
          class="border rounded m-2 pl-10 pr-10 shadow"
          :class="{'cursor-pointer':  notification.status==='NEW'}"
          @click="readNotification(notification)"
      >

        <v-list-item-title>
          <div class="m-1"
               :class="{
            'font-semibold': notification.status==='NEW'
          }">
            {{ notification.title }}
          </div>
        </v-list-item-title>
        <v-list-item-subtitle>
          <div class="m-1">{{ notification.text }}</div>
        </v-list-item-subtitle>


      </v-list-item>
    </v-list>

  </div>

  <div class="text-center py-5">
    <v-pagination
        v-model="page"
        :length="pageNumber"
    ></v-pagination>
  </div>

</template>

<script>
export default {
  name: "NotificationView",
  data() {
    return {
      page: 1,
      pageNumber: 1,
      notifications: []
    }
  },
  watch: {
    page: function () {
      this.loadData()
    }
  },
  methods: {
    loadData() {
      this.$rest.getMyNotifications(
          {
            params: {
              page: this.page - 1
            }
          }
          , (response) => {
            this.pageNumber = response.data.totalPages
            this.notifications = response.data.content
          })
    },
    readNotification(notification) {
      if (notification.status === 'NEW') {
        this.$rest.readNotification(notification.id, () => {
          notification.status = 'READ'
        })
      }
    }
  },
  mounted() {
    this.loadData()
  }
}
</script>

<style scoped>

</style>