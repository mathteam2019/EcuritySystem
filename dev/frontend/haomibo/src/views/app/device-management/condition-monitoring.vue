<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('device-management.condition-monitoring')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">

      <b-tab :title="$t('device-management.condition-monitoring')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4">

              <b-row>
                <b-col xs="12" md="10"  class="d-flex justify-content-between">
                  <div class="flex-grow-1">
                    <b-row>
                      <b-col>
                        <b-form-group :label="$t('device-management.package')">
                          <v-select v-model="package" :options="packageData" :dir="direction"/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('device-management.device-classify')">
                          <v-select v-model="deviceClassify" :options="deviceClassifyData"
                                    :dir="direction"/>
                        </b-form-group>
                      </b-col>
                      <b-col>
                        <b-form-group :label="$t('device-management.device-name')">
                          <b-form-input></b-form-input>
                        </b-form-group>
                      </b-col>
                    </b-row>
                  </div>
                  <div class="align-self-center">
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('device-management.search') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('device-management.reset') }}</b-button>
                  </div>
                </b-col>
                <b-col xs="12" md="2" class="text-md-right">
                  <b-row>
                    <b-colxx xxs="12">
                      <label>{{$t('device-management.switch-image-list')}}</label>
                    </b-colxx>
                    <b-colxx xxs="12">
                      <switches
                        v-model="swichType"
                        theme="custom"
                        color="primary-inverse"
                      ></switches>
                    </b-colxx>
                  </b-row>
                </b-col>
              </b-row>

            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('device-management.business-stat')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4" :title="$t('pages.comments')">
              <h1>Hi</h1>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>


  </div>
</template>
<script>

  import {apiUrl} from "../../../constants/config";
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";
  import Switches from 'vue-switches'
  import Vue from 'vue'

  Vue.component('custom-action', {
    template: [
      '<div>',
      '<button class="ui red button" @click="itemAction(\'view-item\', rowData)"><i class="zoom icon"></i></button>',
      '<button class="ui blue button" @click="itemAction(\'edit-item\', rowData)"><i class="edit icon"></i></button>',
      '<button class="ui green button" @click="itemAction(\'delete-item\', rowData)"><i class="delete icon"></i></button>',
      '</div>'
    ].join(''),

  })

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'switches' : Switches,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        swichType:false,
        package: '',
        deviceClassify: '',
        direction: getDirection().direction,
        packageData: [
          'dsfsf',
          this.$t('device-management.active'),
          this.$t('device-management.inactive'),
          this.$t('device-management.blocked'),
          this.$t('device-management.pending'),
        ],
        deviceClassifyData: [
          this.$t('device-management.headquarters'),
          this.$t('device-management.office'),
          this.$t('device-management.production-department'),
          this.$t('device-management.production-department-1'),
          this.$t('device-management.production-department-2'),
          this.$t('device-management.sales-department'),
          this.$t('device-management.sales-planing-department'),
        ],
      }
    },
    methods: {

    }
  }
</script>
