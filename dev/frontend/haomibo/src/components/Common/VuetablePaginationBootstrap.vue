<template>
  <nav>
    <div style="display: flex;" class="justify-content-between">

      <div style="flex: 1;display: flex;">
        <ul class="pagination justify-content-center pagination-sm mb-0">
          <li :class="{'disabled': isOnFirstPage,'page-item':true}">
            <a class="page-link" href="" @click.prevent="loadPage(1)">
              <span><i class='icofont-double-left'/></span>
            </a>
          </li>
          <li :class="{'disabled': isOnFirstPage,'page-item':true}">
            <a class="page-link" href="" @click.prevent="loadPage('prev')">
              <span><i class='icofont-simple-left'/></span>
            </a>
          </li>
          <template v-if="notEnoughPages">
            <li v-for="n in totalPage" :class="{'active': isCurrentPage(n), 'page-item':true}" :key="n">
              <a class="page-link" @click.prevent="loadPage(n)" v-html="n"></a>
            </li>
          </template>
          <template v-else>
            <li v-for="n in windowSize" :class="{'active': isCurrentPage(windowStart+n-1), 'page-item':true}" :key="n">
              <a class="page-link" @click.prevent="loadPage(windowStart+n-1)" v-html="windowStart+n-1"></a>
            </li>
          </template>
          <li :class="{'disabled': isOnLastPage,'page-item':true}">
            <a class="page-link" href="" @click.prevent="loadPage('next')">
              <span><i class='icofont-simple-right'/></span>
            </a>
          </li>
          <li :class="{'disabled': isOnLastPage,'page-item':true}">
            <a class="page-link" href="" @click.prevent="tablePagination && loadPage(tablePagination.last_page)">
              <span><i class='icofont-double-right'/></span>
            </a>
          </li>

        </ul>
      </div>
      <div style="flex: 1;display: flex; justify-content: flex-end;align-items: center;">

        <div>
          <span class="mr-3">{{$t('vuetable.every-page-showing')}}</span>
          <select
            v-model="perPage"
            class="mr-3 form-control d-inline"
            style="width: 48px;">
            <option>5</option>
            <option>10</option>
            <option>15</option>
            <option>20</option>
            <option>25</option>
          </select>
          <span class="mr-3">{{$t('vuetable.items')}}</span>
          <span class="mr-3">/</span>
          <span class="mr-3">{{$t('vuetable.total')}}</span>
          <span class="mr-3">{{tablePagination ? tablePagination.total : 0}}</span>
          <span class="mr-3">{{$t('vuetable.record')}}</span>
        </div>

      </div>
    </div>
  </nav>
</template>

<script>
  import VuetablePaginationMixin from 'vuetable-2/src/components/VuetablePaginationMixin'

  export default {
    mixins: [VuetablePaginationMixin],
    props: {
      initialPerPage: {
        type: Number,
        default: 10
      },
    },
    data() {
      return {
        perPage: this.initialPerPage,
        putDownPageSizeBar: this.putDownPageSizeBar
      };
    },
    watch: {
      perPage(newVal) {

        this.$emit('onUpdatePerPage', newVal);

      }
    }
  }
</script>
