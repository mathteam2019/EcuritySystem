<template>
  <div class="vue-dual-list">
    <div class="h-100 flex-row d-flex">
      <div class="d-flex flex-grow-1 flex-column dual-list-column">
        <b-form-group class="mt-2" :label="$t('menu.device-classify')">
          <b-form-select v-model="availableFilter"  :options="filterOptions" plain></b-form-select>
        </b-form-group>
        <div class='list' v-bind:class="options.resizeBox">
          <div class="d-block">
            {{$t('device-management.before-selected')}}
          </div>
          <ul class='pd'>
            <li class="mb-1" v-for='item in availableFilterData' :key="item.name"
                @click='transferToRight(options.items.indexOf(item))'>
              {{ item.name }}
            </li>
          </ul>
        </div>
      </div>
      <div class="d-flex flex-grow-0 justify-content-center align-items-center flex-column">
        <div class="move-button-container">
          <span  @click='transferToRight(options.items.indexOf(item))'><i :class="options.isLtr === 'ltr'?'icofont-long-arrow-right':'icofont-long-arrow-left'"></i></span>
          <span @click='transferToLeft(-1)'><i :class="options.isLtr === 'rtl'?'icofont-long-arrow-right':'icofont-long-arrow-left'"></i></span>
        </div>
      </div>
      <div class="d-flex flex-grow-1 flex-column dual-list-column">
        <b-form-group class="mt-2" :label="$t('menu.device-classify')">
          <b-form-select v-model="appliedFilter" :options="filterOptions" plain></b-form-select>
        </b-form-group>
        <div class='list' v-bind:class="options.resizeBox">
          <div class="d-block">
            {{$t('device-management.selected')}}
          </div>
          <ul class='pd'>
            <li class="mb-1" v-for='item in appliedFilterData' :key="item.name"
                @click='transferToLeft(options.selectedItems.indexOf(item))'>
              {{ item.name }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss">
  .vue-dual-list {

    $item-horizontal-margin: .8rem;
    $move-button-color: #178af7;

    .form-control {
      max-width: none !important;
    }

    .move-button-container {
      display: flex;
      flex-direction: column;

      span {
        padding: 5px;
        border: solid 1px #3182eb;
        border-radius: 3px;
        background: #3182eb;
        height: 32px;
        width: 32px;
        text-align: center;
        color: white;
        cursor: pointer;
        margin: 14px;
        i {
          font-size: 22px;
        }
      }
    }

    .dual-list-column {
      $dual-list-column-margin: 1.5rem;
      margin-left: $dual-list-column-margin;
      margin-right: $dual-list-column-margin;
      flex-basis: 0;
    }

    .list {
      border: 1px solid #cccccc;
      border-radius: 4px;
      overflow-y: auto;
      height: 100%;
      background-color: white;

      & > div {
        padding: $item-horizontal-margin/2 #{$item-horizontal-margin - .2rem};
        font-size: 1rem;
        background-color: #ebebeb;
        border-bottom: 1px solid #e5e5e5;
      }

      ul.pd {
        padding-left: 0px;
        list-style-type: none !important;
        width: 100%;
        font-size: 1rem;
        margin-top: 1rem;
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


  export default {
    name: 'vue-dual-list',
    components: {},
    props: {
      'options': {
        type: Object,
        isLtr: 'ltr',
        required: true
      }
    },
    data: function () {
      return {
        filterOptions: [],
        availableFilter: null,
        appliedFilter: null,
        item: {}
      }
    },
    methods: {
      transferToRight: function (index) {
        //this.search = ''
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
        if (index >= 0) {
          this.options.items.push(this.options.selectedItems[index])
          this.options.selectedItems.splice(index, 1)
          return
        }
        for (var cont = 0; cont < this.options.selectedItems.length; cont++) {
          this.options.items.push(this.options.selectedItems[cont])
        }
        this.options.selectedItems.splice(0, this.options.selectedItems.length)
      },
      setAvailableItem: function (data = []) {
        this.options.items = data;
      },
      setAppliedItem: function (data = []) {
        this.options.selectedItems = data;
      },
      setFilterOptions: function (data = []) {
        this.filterOptions = data;
      },
      resetFilterOption:function () {
        this.availableFilter = null;
        this.appliedFilter = null;
      }
    },
    computed: {
      availableFilterData: function () {
        if (this.availableFilter) {
          return this.options.items.filter((item) => {
            return item.category === this.availableFilter
            //return item.name.toLowerCase().indexOf(this.search) !== -1
          })
        }
        return this.options.items
      },
      appliedFilterData: function () {
        if (this.appliedFilter) {
          return this.options.selectedItems.filter((item) => {
            return item.category === this.appliedFilter
            //return item.name.toLowerCase().indexOf(this.search) !== -1
          })
        }
        return this.options.selectedItems
      }
    }
  }
</script>
