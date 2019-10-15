<template>
  <div class="vue-dual-list">
    <b-row>
      <b-col cols="5">
        <label>{{$t('device-management.dispatched-device')}}</label>
        <div class='list d-flex' v-bind:class="options.resizeBox">
          <ul class='pd'>
            <li class="mb-1" v-for='item in filtering' :key="item.name">
              <a href='javascript:void(0)' class="badge badge-info" v-on:click='transferToRight(options.items.indexOf(item))'
                 >
                {{ item.name }}</a>
            </li>
          </ul>
        </div>
      </b-col>
      <b-col cols="2" class="d-flex justify-content-center align-items-center flex-column">
        <b-button class="btn btn-info default m-1" @click='transferToRight(options.items.indexOf(item))'><i
          v-bind:class="{'iconsminds-arrow-right-2': options.isLtr==='ltr','iconsminds-arrow-left-2':options.isLtr!=='ltr'}"></i>
        </b-button>
        <b-button class="btn btn-info default m-1" @click='transferToLeft(-1)'><i v-bind:class="{'iconsminds-arrow-right-2': options.isLtr!=='ltr','iconsminds-arrow-left-2':options.isLtr==='ltr'}"></i>
        </b-button>
      </b-col>
      <b-col cols="5">
        <label>{{$t('device-management.pending-device')}}</label>
        <div class='list d-flex' v-bind:class="options.resizeBox">
          <ul class='pd'>
            <li class="mb-1" v-for='item in options.selectedItems' :key="item.name">
              <a href='javascript:void(0)' class="badge badge-danger" v-on:click='transferToLeft(options.selectedItems.indexOf(item))'
                 >
                {{ item.name }}
              </a>
            </li>
          </ul>
        </div>
      </b-col>
    </b-row>
  </div>
</template>

<style lang="css">
  .vue-dual-list .list {
    border: 1px solid #999;
    border-radius: 4px;
    padding: 10px;
    overflow-y: auto;
    max-height: 400px;
  }

  .vue-dual-list ul.pd {
    padding-left: 12px;
    list-style-type: none!important;
  }
  .vue-dual-list ul>li>a:hover {
    text-decoration: none!important;
  }
</style>

<script>
  import Vue from 'vue'
  import Buttons from "../../../../theme_4_reference/src/views/app/ui/components/Buttons";

  export default {
    name: 'vue-dual-list',
    components: {Buttons},
    props: {
      'options': {
        type: Object,
        default: {isLtr: 'rtl'},
        required: true
      }
    },
    data: function () {
      return {
        search: '',
        item: {}
      }
    },
    methods: {
      transferToRight: function (index) {
        this.search = ''
        if (index >= 0) {
          this.options.selectedItems.push(this.options.items[index])
          this.options.items.splice(index, 1)
        } else {
          for (var cont = 0; cont < this.options.items.length; cont++) {
            this.options.selectedItems.push(this.options.items[cont])
          }

          this.options.items.splice(0, this.options.items.length)
        }
      },
      transferToLeft: function (index) {
        this.search = ''
        if (index >= 0) {
          this.options.items.push(this.options.selectedItems[index])
          this.options.selectedItems.splice(index, 1)
          return
        }
        for (var cont = 0; cont < this.options.selectedItems.length; cont++) {
          this.options.items.push(this.options.selectedItems[cont])
        }
        this.options.selectedItems.splice(0, this.options.selectedItems.length)
      }
    },
    computed: {
      filtering: function () {
        if (this.search) {
          return this.options.items.filter((item) => {
            return item.name.toLowerCase().indexOf(this.search) !== -1
          })
        }
        return this.options.items
      }
    }
  }
</script>
