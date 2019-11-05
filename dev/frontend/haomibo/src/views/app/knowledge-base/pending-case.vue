<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-card>
      <b-row>
        <b-col cols="8">
          <b-row>
            <b-col>
              <b-form-group :label="$t('knowledge-base.pending-case.device-log.device')">
                <b-form-input v-model="filterForm.device"></b-form-input>
              </b-form-group>
            </b-col>

            <b-col>
              <b-form-group :label="$t('knowledge-base.pending-case.device-log.user')">
                <b-form-input v-model="filterForm.user"></b-form-input>
              </b-form-group>
            </b-col>

            <b-col>
              <b-form-group :label="$t('knowledge-base.pending-case.device-log.category')">
                <b-form-input v-model="filterForm.category"></b-form-input>
              </b-form-group>
            </b-col>

            <b-col>
              <b-form-group :label="$t('knowledge-base.pending-case.device-log.level')">
                <b-form-input v-model="filterForm.level"></b-form-input>
              </b-form-group>
            </b-col>
            <b-col class="d-flex align-items-center" style="padding-top: 10px;">
                      <span class="rounded-span flex-grow-0 text-center text-light" @click="isExpanded = !isExpanded" >
                        <i :class="!isExpanded?'icofont-rounded-down':'icofont-rounded-up'"></i>
                      </span>
            </b-col>
          </b-row>
        </b-col>
        <b-col cols="8" v-if="isExpanded">
          <b-row>
            <b-col>
              <b-form-group :label="$t('knowledge-base.pending-case.device-log.start-time')">
                <b-form-input v-model="filterForm.startTime"></b-form-input>
              </b-form-group>
            </b-col>
            <b-col></b-col>
            <b-col></b-col>
            <b-col></b-col>
            <b-col></b-col>
          </b-row>
        </b-col>
        <b-col cols="4" class="d-flex justify-content-end align-items-center">
          <div>
            <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
              <i class="icofont-search-1"></i>&nbsp;{{ $t('log-management.search') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
              <i class="icofont-ui-reply"></i>&nbsp;{{$t('log-management.reset') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default">
              <i class="icofont-share-alt"></i>&nbsp;{{ $t('log-management.export') }}
            </b-button>
            <b-button size="sm" class="ml-2" variant="outline-info default">
              <i class="icofont-printer"></i>&nbsp;{{ $t('log-management.print') }}
            </b-button>
          </div>
        </b-col>
      </b-row>
    </b-card>

  </div>
</template>
<script>
  import _ from 'lodash';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        pageStatus: 'list',
        isExpanded:false,
        filterForm: {
          userName: null,
          licenseNumber: null,
          grade: null
        },
      }
    },
    methods: {

      onSearchButton(){

      },
      onResetButton(){

      },
    }
  }
</script>


