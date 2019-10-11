<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.document-template')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>
    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">
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
                    <b-button type="submit" variant="primary" class="mt-2 px-5">{{ $t('device-management.save') }}</b-button>
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
          <b-colxx>

          </b-colxx>
        </b-row>
      </b-tab>
    </b-tabs>
  </div>
</template>
<script>
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {apiUrl} from '../../../constants/config'
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import { getDirection } from '../../../utils'

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    data() {
      return {
        direction: getDirection().direction,
        searchType: 'chocolate',
        typeData: [
          {label: 'Chocolate', value: 'chocolate'},
          {label: 'Vanilla', value: 'vanilla'},
          {label: 'Strawberry', value: 'strawberry'}
        ],
        basicForm: {
          templateName: '',
          templateNumber: '',
          deviceClassify: '',
          manufacture: '',
          originModel: '',
        },
        deviceClassifyData: [
          {label: 'Chocolate', value: 'chocolate'},
          {label: 'Vanilla', value: 'vanilla'},
          {label: 'Strawberry', value: 'strawberry'},
          {label: 'Caramel', value: 'caramel'},
          {label: 'Cookies and Cream', value: 'cookiescream'},
          {label: 'Peppermint', value: 'peppermint'}
        ],
        selectData: [
          {label: 'Chocolate', value: 'chocolate'},
          {label: 'Vanilla', value: 'vanilla'},
          {label: 'Strawberry', value: 'strawberry'},
          {label: 'Caramel', value: 'caramel'},
          {label: 'Cookies and Cream', value: 'cookiescream'},
          {label: 'Peppermint', value: 'peppermint'}
        ],
        vuetableItems: {
          apiUrl: apiUrl + '/cakes/fordatatable',
          fields: [
            {
              name: 'title',
              sortField: 'title',
              title: this.$t('device-management.no'),
              titleClass: '',
              dataClass: 'list-item-heading'
            },
            {
              name: 'sales',
              sortField: 'sales',
              title: this.$t('device-management.template-number'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'stock',
              sortField: 'stock',
              title: this.$t('device-management.template-name'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'category',
              sortField: 'category',
              title: this.$t('device-management.device-classify'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'category',
              sortField: 'category',
              title: this.$t('device-management.setting'),
              titleClass: '',
              dataClass: 'text-muted'
            },
            {
              name: 'category',
              sortField: 'category',
              title: this.$t('device-management.effective'),
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
