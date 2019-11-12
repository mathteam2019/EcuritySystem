<template>
  <div class="vue-dual-list">
    <b-row>
      <b-col cols="5">
        <b-form-group class="mt-2" :label="$t('menu.device-classify')">
          <b-form-select :options="[]" plain></b-form-select>
        </b-form-group>
        <div class='list' v-bind:class="options.resizeBox">
          <div class="d-block">
            {{$t('device-management.before-selected')}}
          </div>
          <ul class='pd'>
            <li class="mb-1" v-for='item in filtering' :key="item.name" @click='transferToRight(options.items.indexOf(item))'>
                {{ item.name }}
            </li>
          </ul>
        </div>
      </b-col>
      <b-col cols="2" class="d-flex justify-content-center align-items-center flex-column">
        <b-button size="sm" variant="info default m-1" @click='transferToRight(options.items.indexOf(item))'>
          <i class="icofont-long-arrow-right"></i>
        </b-button>
          <b-button size="sm" variant="info default m-1" @click='transferToLeft(-1)'>
          <i class="icofont-long-arrow-left"></i>
        </b-button>
      </b-col>
      <b-col cols="5">
        <b-form-group class="mt-2" :label="$t('menu.device-classify')">
          <b-form-select :options="[]" plain></b-form-select>
        </b-form-group>
        <div class='list' v-bind:class="options.resizeBox">
          <div class="d-block">
            {{$t('device-management.selected')}}
          </div>
          <ul class='pd'>
            <li class="mb-1" v-for='item in options.selectedItems' :key="item.name" @click='transferToLeft(options.selectedItems.indexOf(item))'>
              {{ item.name }}
            </li>
          </ul>
        </div>
      </b-col>
    </b-row>
  </div>
</template>

<style lang="scss">
  .vue-dual-list {

    $item-horizontal-margin: .8rem;
    $move-button-color: #178af7;

    .form-control {
      max-width: none!important;
    }

    button {
      background-color: $move-button-color;
      border-width: 0;
      width: 36px;
      padding-left: 13px!important;

      &:hover {
        background-color: $move-button-color;
      }
    }

    .list {
      border: 1px solid #999;
      border-radius: 4px;
      overflow-y: auto;
      height: 100%;

      &>div {
        padding: $item-horizontal-margin/2 #{$item-horizontal-margin - .2rem};
        font-size: 1rem;
        background-color: #ebebeb;
        border-bottom: 1px solid #e5e5e5;
      }

      ul.pd {
        padding-left: 0px;
        list-style-type: none!important;
        width: 100%;
        font-size: 1rem;

        li {
          padding: $item-horizontal-margin/2 $item-horizontal-margin;
          margin: 0px;
          cursor: pointer;

          &:hover {
            background-color: #f4f4f4;
          }
        }
      }
    }
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
