<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb />
        </b-colxx>
      </b-row>
    </div>
    <b-tabs nav-class="ml-2" :no-fade="true">
      <b-tab :title="$t('device-management.file-template')">
        <b-row>
          <b-col xxs="12" md="4" lg="4">
            <b-card class="mb-4" no-body>

              <b-card-body>
                <b-form @submit.prevent="onBasicSubmit">
                  <b-form-group :label="$t('device-management.template-number')">
                    <b-form-input type="text" v-model="basicForm.templateNumber"
                                  :placeholder="$t('device-management.template-number-placeholder')"/>
                  </b-form-group>
                  <b-form-group :label="$t('device-management.template-name')">
                    <b-form-input type="text" v-model="basicForm.templateName"
                                  :placeholder="$t('device-management.template-name-placeholder')"/>
                  </b-form-group>
                  <b-form-group :label="$t('device-management.device-classify')">
                    <v-select v-model="basicForm.deviceClassify" :options="deviceClassifyData" :dir="direction"
                              :placeholder="$t('device-management.device-classify-placeholder')"/>
                  </b-form-group>
                  <b-form-group :label="$t('device-management.manufacture')">
                    <v-select v-model="basicForm.manufacture" :options="selectData" :dir="direction"
                    />
                  </b-form-group>
                  <b-form-group :label="$t('device-management.origin-model')">
                    <b-form-input type="text" v-model="basicForm.originModel"
                                  :placeholder="$t('device-management.origin-model-placeholder')"/>
                  </b-form-group>
                  <b-form-group class="text-right">
                    <b-button type="submit" variant="primary" class="mt-2">{{ $t('device-management.save') }}</b-button>
                  </b-form-group>

                </b-form>
              </b-card-body>
            </b-card>

          </b-col>
          <b-col xxs="12" md="8" lg="8">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-col cols="12" md="4">
                    <b-form-group>
                      <v-select v-model="searchType" :options="typeData" :dir="direction"/>
                    </b-form-group>
                  </b-col>
                  <b-col cols="12" md="8">

                  </b-col>
                </b-row>
                <b-row>
                  <b-colxx class="table-responsive  table-striped ">
                    <vuetable
                      class="thead-dark"
                      ref="vuetable"
                      :api-url="vuetableItems.apiUrl"
                      :fields="vuetableItems.fields"
                      :search-options="{
                          enabled: true
                      }"
                      :per-page="5"
                      pagination-path
                      @vuetable:pagination-data="onPaginationData"
                    ></vuetable>
                    <vuetable-pagination-bootstrap
                      ref="pagination"
                      @vuetable-pagination:change-page="onChangePage"
                    ></vuetable-pagination-bootstrap>
                  </b-colxx>
                </b-row>
              </b-card-body>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('device-management.business-stat')">
        <b-row>
          <b-col xxs="12" md="4" lg="4">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-colxx class="mb-4">
                    <h6 class="card-subtitle">Pie</h6>
                    <div class="chart-container">
                      <pie-shadow-chart :data="pieChartData" :height="300" />
                    </div>
                  </b-colxx>
                </b-row>
              </b-card-body>
            </b-card>

          </b-col>
          <b-col xxs="12" md="8" lg="8">
            <b-card class="mb-4" no-body>
              <b-card-body>
                <b-row>
                  <b-colxx class="mb-4">
                    <h6 class="card-subtitle">Bar-1</h6>
                    <div class="chart-container">
                      <bar-shadow-chart :data="barChartData" :height="300" />
                    </div>
                  </b-colxx>
                  <b-colxx class="mb-4">
                    <h6 class="card-subtitle">Bar-2</h6>
                    <div class="chart-container">
                      <bar-shadow-chart :data="barChartData" :height="300" />
                    </div>
                  </b-colxx>
                </b-row>
              </b-card-body>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
  </div>
</template>
<script>
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {apiBaseUrl} from '../../../constants/config'
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import { getDirection } from '../../../utils'
  import {
    barChartData,
    pieChartData
  } from '../../../data/charts'
  import BarShadowChart from '../../../components/Charts/BarShadow'
  import PieShadowChart from '../../../components/Charts/PieShadow'
  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'pie-shadow-chart': PieShadowChart,
      'bar-shadow-chart': BarShadowChart
    },
    data() {
      return {
        direction: getDirection().direction,
        searchType: '全部',
        barChartData,
        pieChartData,
        typeData: [
          {label: this.$t('device-management.all'), value: ''},
          {label: this.$t('device-management.active'), value: 'Active'},
          {label: this.$t('device-management.inactive'), value: 'Inactive'}
        ],
        basicForm: {
          templateName: '',
          templateNumber: '',
          deviceClassify: '',
          manufacture: '',
          originModel: '',
        },
        deviceClassifyData: [
          {label: "人体查验设备", value: 'chocolate'},
          {label: "物品查验设备", value: 'vanilla'},
          {label: "车辆查验设备", value: 'vanilla'}
        ],
        selectData: [
          {label: "同方威视", value: 'chocolate'},
          {label: "海康威视", value: 'chocolate'}
        ],
        vuetableItems: {
          apiUrl: apiBaseUrl + '/cakes/fordatatable',
          fields: [
            {
              name: 'no',
              sortField: 'no',
              title: this.$t('device-management.no'),
              titleClass: '',
              dataClass: 'list-item-heading'
            },
            {
              name: 'number',
              sortField: 'number',
              title: this.$t('device-management.template-number'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'name',
              sortField: 'name',
              title: this.$t('device-management.template-name'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'classify',
              sortField: 'classify',
              title: this.$t('device-management.device-classify'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'setting',
              sortField: 'setting',
              title: this.$t('device-management.setting'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'active',
              sortField: 'active',
              title: this.$t('device-management.active'),
              titleClass: '',
              dataClass: 'text-muted'
            }
          ]
        },
      }
    },
    methods: {
      onBasicSubmit() {
        console.log(JSON.stringify(this.basicForm))
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
    }
  }
</script>
