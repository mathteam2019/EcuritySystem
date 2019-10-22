<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.permission-control')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">

      <b-tab :title="$t('permission-management.permission-control.role-setting')">
        <b-row>
          <b-col cols="3">
            <b-card class="mb-4">
              <b-form>
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.role-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input :placeholder="$t('permission-management.permission-control.enter-role-name')" />
                </b-form-group>
                <b-form-group :label="$t('permission-management.permission-control.note')">
                  <b-form-textarea rows="3" :placeholder="$t('permission-management.permission-control.enter-note')"></b-form-textarea>
                </b-form-group>
                <b-row class="mt-4">
                  <b-col cols="12" class="text-right">
                    <b-button type="submit" variant="primary">{{ $t('permission-management.permission-control.save') }}</b-button>
                  </b-col>
                </b-row>
              </b-form>
            </b-card>
          </b-col>
          <b-col cols="5">
            <b-card class="mb-4">
              <b-row>
                <b-col cols="5" class="pr-3">
                  <b-form-group :label="$t('permission-management.permission-control.role-flag')">
                    <v-select v-model="roleFlag" :options="roleFlagData" :dir="direction"/>
                  </b-form-group>
                </b-col>

                <b-col cols="7">
                  <b-form-group>
                    <template slot="label">&nbsp;</template>
                    <b-form-input></b-form-input>
                  </b-form-group>
                </b-col>
              </b-row>

              <b-row>
                <b-col cols="12">
                  <vuetable
                    ref="vuetable"
                    :api-mode="false"
                    :fields="roleItems.fields"
                    :data-manager="dataManager"
                    :per-page="5"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onPaginationData"
                  >

                    <template slot="actions" slot-scope="props">
                      <div>

                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="info"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-modify') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='inactive'"
                          size="sm"
                          variant="info"
                          disabled>
                          {{ $t('permission-management.action-modify') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="success"
                          @click="onAction('make-active', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-make-active') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='active'"
                          size="sm"
                          variant="warning"
                          @click="onAction('make-inactive', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-make-inactive') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='inactive' && props.rowData.status!='active'"
                          size="sm"
                          variant="success"
                          disabled>
                          {{ $t('permission-management.action-make-active') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="danger"
                          @click="onAction('block', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-block') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='blocked'"
                          size="sm"
                          variant="success"
                          @click="onAction('unblock', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-unblock') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status!='inactive' && props.rowData.status!='blocked'"
                          size="sm"
                          variant="danger"
                          disabled>
                          {{ $t('permission-management.action-block') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='pending'"
                          size="sm"
                          variant="dark"
                          @click="onAction('reset-password', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-reset-password') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='pending'"
                          size="sm"
                          variant="dark"
                          disabled>
                          {{ $t('permission-management.action-reset-password') }}
                        </b-button>

                      </div>
                    </template>

                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onChangePage"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="4">
            <b-card class="mb-4">

              <b-row>
                <b-col class="d-flex">
                  <div class="flex-grow-1">

                    <b-row>

                      <b-col>
                        <b-form-group :label="$t('permission-management.username')">
                          <b-form-input></b-form-input>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.status')">
                          <v-select v-model="selectedStatus" :options="statusSelectData" :dir="direction"/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.affiliated-institution')">
                          <v-select v-model="selectedAffiliatedInstitution" :options="affiliatedInstitutionSelectData"
                                    :dir="direction"/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.user-category')">
                          <v-select v-model="selectedUserCategory" :options="userCategorySelectData" :dir="direction"/>
                        </b-form-group>
                      </b-col>
                      <b-col></b-col>
                    </b-row>

                  </div>
                  <div class="align-self-center">
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('permission-management.search') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('permission-management.reset') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="success">{{ $t('permission-management.new') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info">{{ $t('permission-management.print') }}
                    </b-button>
                  </div>
                </b-col>
              </b-row>

              <b-row>
                <b-col cols="12">
                  <vuetable
                    ref="vuetable"
                    :api-mode="false"
                    :fields="roleItems.fields"
                    :data-manager="dataManager"
                    :per-page="5"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onPaginationData"
                  >

                    <template slot="actions" slot-scope="props">
                      <div>

                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="info"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-modify') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='inactive'"
                          size="sm"
                          variant="info"
                          disabled>
                          {{ $t('permission-management.action-modify') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="success"
                          @click="onAction('make-active', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-make-active') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='active'"
                          size="sm"
                          variant="warning"
                          @click="onAction('make-inactive', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-make-inactive') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='inactive' && props.rowData.status!='active'"
                          size="sm"
                          variant="success"
                          disabled>
                          {{ $t('permission-management.action-make-active') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="danger"
                          @click="onAction('block', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-block') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='blocked'"
                          size="sm"
                          variant="success"
                          @click="onAction('unblock', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-unblock') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status!='inactive' && props.rowData.status!='blocked'"
                          size="sm"
                          variant="danger"
                          disabled>
                          {{ $t('permission-management.action-block') }}
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='pending'"
                          size="sm"
                          variant="dark"
                          @click="onAction('reset-password', props.rowData, props.rowIndex)">
                          {{ $t('permission-management.action-reset-password') }}
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='pending'"
                          size="sm"
                          variant="dark"
                          disabled>
                          {{ $t('permission-management.action-reset-password') }}
                        </b-button>

                      </div>
                    </template>

                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onChangePage"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.permission-control.data-grouping')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4" :title="'TODO'">
              <h1>Hi</h1>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>


  </div>
</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import axios from 'axios'
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";
  import _ from "lodash";

  import staticUserTableData from '../../../data/user'

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mounted() {
      this.tableData = staticUserTableData;
    },
    data() {
      return {
        roleFlag: '',
        roleFlagData: [
            this.$t('permission-management.permission-control.all'),
            this.$t('permission-management.permission-control.system-management'),
            this.$t('permission-management.permission-control.business-operating'),
            this.$t('permission-management.permission-control.no-role'),
        ],


        tableData: [],
        selectedStatus: '',
        selectedAffiliatedInstitution: '',
        selectedUserCategory: '',

        direction: getDirection().direction,
        statusSelectData: [
          this.$t('permission-management.all'),
          this.$t('permission-management.active'),
          this.$t('permission-management.inactive'),
          this.$t('permission-management.blocked'),
          this.$t('permission-management.pending'),
        ],
        affiliatedInstitutionSelectData: [
          this.$t('permission-management.headquarters'),
          this.$t('permission-management.office'),
          this.$t('permission-management.production-department'),
          this.$t('permission-management.production-department-1'),
          this.$t('permission-management.production-department-2'),
          this.$t('permission-management.sales-department'),
          this.$t('permission-management.sales-planing-department'),
        ],
        userCategorySelectData: [
          this.$t('permission-management.all'),
          this.$t('permission-management.admin'),
          this.$t('permission-management.normal-staff'),
        ],
        items: [
          {id: 1, first_name: 'Mark', last_name: 'Otto', username: '@mdo'},
          {id: 2, first_name: 'Jacob', last_name: 'Thornton', username: '@fat'},
          {id: 3, first_name: 'Lary', last_name: 'the Bird', username: '@twitter'}
        ],
        roleItems: {
          apiUrl: apiBaseUrl + '/cakes/fordatatable',
          fields: [
            {
              name: 'no',
              title: this.$t('permission-management.permission-control.serial-number'),
              sortField: 'no',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: this.$t('permission-management.permission-control.role-name'),
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'username',
              title: this.$t('permission-management.permission-control.role-flag'),
              sortField: 'username',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              title: this.$t('permission-management.permission-control.operating'),
              sortField: 'status',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {

                const dictionary = {
                  "active": `<span class="text-success">${this.$t('permission-management.active')}</span>`,
                  "inactive": `<span class="text-dark">${this.$t('permission-management.inactive')}</span>`,
                  "blocked": `<span class="text-danger">${this.$t('permission-management.blocked')}</span>`,
                  "pending": `<span class="text-warning">${this.$t('permission-management.pending')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'affiliatedInstitution',
              title: this.$t('permission-management.permission-control.note'),
              sortField: 'affiliatedInstitution',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
          ]
        },
        currentPage: 1,
        perPage: 5,
        totalRows: 0,
        bootstrapTable: {
          selected: [],
          selectMode: 'multi',
          fields: [
            {key: 'title', label: 'Title', sortable: true, sortDirection: 'desc', tdClass: 'list-item-heading'},
            {key: 'sales', label: 'Sales', sortable: true, tdClass: 'text-muted'},
            {key: 'stock', label: 'Stock', sortable: true, tdClass: 'text-muted'},
            {key: 'category', label: 'Category', sortable: true, tdClass: 'text-muted'},
            {key: 'status', label: 'Status', sortable: true, tdClass: 'text-muted'}
          ]
        }
      }
    },
    watch: {
      tableData(newVal, oldVal) {
        this.$refs.vuetable.refresh();
      }
    },
    methods: {
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      rowSelected(items) {
        this.bootstrapTable.selected = items
      },
      dataManager(sortOrder, pagination) {

        if (this.tableData.length < 1) return;

        let local = this.tableData;

        for (let i = 0; i < local.length; i++) {
          local[i].no = i + 1;
        }

        // sortOrder can be empty, so we have to check for that as well
        if (sortOrder.length > 0) {
          local = _.orderBy(
            local,
            sortOrder[0].sortField,
            sortOrder[0].direction
          );
        }

        pagination = this.$refs.vuetable.makePagination(
          local.length,
          this.perPage
        );
        let from = pagination.from - 1;
        let to = from + this.perPage;

        return {
          pagination: pagination,
          data: _.slice(local, from, to)
        };

      },
      onAction(action, data, index) {
        console.log('(slot) action: ' + action, data, index)
      }
    }
  }
</script>
