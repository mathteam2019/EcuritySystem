<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.permission-control')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs v-show="!isLoading" nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">

      <b-tab :title="$t('permission-management.permission-control.role-setting')">
        <b-row>
          <b-col cols="3">
            <b-card class="mb-4">
              <b-form @submit.prevent="onRoleFormSubmit">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.role-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="roleForm.roleName"
                    :state="!$v.roleForm.roleName.$invalid"
                    :placeholder="$t('permission-management.permission-control.enter-role-name')" />
                  <div v-if="!$v.roleForm.roleName.$invalid">&nbsp;</div>
                  <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}</b-form-invalid-feedback>

                </b-form-group>
                <b-form-group :label="$t('permission-management.permission-control.note')">
                  <b-form-textarea v-model="roleForm.note" rows="3" :placeholder="$t('permission-management.permission-control.enter-note')"></b-form-textarea>
                </b-form-group>
                <b-row class="mt-4">
                  <b-col cols="12" class="text-right">
                    <b-button type="submit" :disabled="$v.roleForm.$invalid" variant="primary">{{ $t('permission-management.permission-control.save') }}</b-button>
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
                <b-form-group>
                  <b-form-radio-group>
                    <b-form-radio value="first">统一管理平台</b-form-radio>
                    <b-form-radio value="second">综合业务平台</b-form-radio>
                  </b-form-radio-group>
                </b-form-group>
              </b-row>

              <b-row>
                <b-col cols="12" class="text-right">
                  <b-form-group>
                    <b-form-checkbox>{{$t('permission-management.permission-control.select-all')}}</b-form-checkbox>
                  </b-form-group>
                </b-col>
                <b-col cols="12">
                  <v-tree ref='accessTree' :data='accessTreeData' :multiple="true" :halfcheck='true' />
                </b-col>
                <b-col cols="12" class="text-right">
                  <b-form-group>
                    <b-button>{{$t('permission-management.permission-control.save')}}</b-button>
                  </b-form-group>
                </b-col>
              </b-row>

            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.permission-control.data-grouping')">
        <b-row>
          <b-col cols="3">
            <b-card class="mb-4">
              <b-form @submit.prevent="onDataGroupFormSubmit">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.data-group-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="dataGroupForm.dataGroupName"
                    :state="!$v.dataGroupForm.dataGroupName.$invalid"
                    :placeholder="$t('permission-management.permission-control.enter-data-group-name')" />
                  <div v-if="!$v.dataGroupForm.dataGroupName.$invalid">&nbsp;</div>
                  <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}</b-form-invalid-feedback>

                </b-form-group>
                <b-form-group :label="$t('permission-management.permission-control.note')">
                  <b-form-textarea v-model="dataGroupForm.note" rows="3" :placeholder="$t('permission-management.permission-control.enter-note')"></b-form-textarea>
                </b-form-group>
                <b-row class="mt-4">
                  <b-col cols="12" class="text-right">
                    <b-button type="submit" :disabled="$v.dataGroupForm.$invalid" variant="primary">{{ $t('permission-management.permission-control.save') }}</b-button>
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
                    ref="dataGroupVuetable"
                    :api-url="dataGroupVuetableItems.apiUrl"
                    :http-fetch="dataGroupVuetableHttpFetch"
                    :fields="dataGroupVuetableItems.fields"
                    :per-page="dataGroupVuetableItems.perPage"
                    pagination-path="data"
                    data-path="data.data"
                    :row-class="onDataGroupRowClass"
                    @vuetable:pagination-data="onDataGroupPaginationData"
                    @vuetable:row-clicked="onDataGroupRowClicked"
                  >

                    <template slot="dataGroupFlag" slot-scope="props">
                      <div v-if="props.rowData.users.length" class="glyph-icon simple-icon-notebook text-info tb-icon"></div>
                    </template>

                    <template slot="operating" slot-scope="props">
<!--                      @click="onAction('delete', props.rowData)"-->
                      <div v-if="!props.rowData.users.length" class="glyph-icon simple-icon-close text-danger tb-button"></div>
                      <div v-if="props.rowData.users.length" class="glyph-icon simple-icon-close tb-button-disabled"></div>
                    </template>

                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="dataGroupPagination"
                    @vuetable-pagination:change-page="onChangePage"
                    :initial-per-page="dataGroupVuetableItems.perPage"
                    @onUpdatePerPage="dataGroupVuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="4">
            <b-card class="mb-4" v-if="selectedDataGroup">

              <b-row>
                <b-col class="text-right">
                  <b-form-group>
                    <b-form-checkbox v-model="isSelectedAllUsersForDataGroup">
                      {{$t('permission-management.permission-control.select-all')}}
                    </b-form-checkbox>
                  </b-form-group>
                </b-col>
              </b-row>

              <b-row>
                <b-col>
                  <v-tree ref='accessTree' :data='orgUserTreeData' :multiple="true" :halfcheck='true' />
                </b-col>
              </b-row>

              <b-row>
                <b-col cols="12" class="text-right">
                  <b-form-group>
                    <b-button>{{$t('permission-management.permission-control.save')}}</b-button>
                  </b-form-group>
                </b-col>
              </b-row>

            </b-card>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>

    <div v-show="isLoading" class="loading"></div>

  </div>
</template>

<style>
  .halo-tree .inputCheck {
    top: 2px!important;
  }
  .tb-icon {
    font-size: 20px;
  }
  .tb-button {
    font-size: 20px;
    cursor: pointer;
  }
  .tb-button-disabled {
    font-size: 20px;
    color: lightgray!important;
  }
  .selected-row {
    background-color: #0000ff20!important;
  }
</style>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import axios from 'axios'
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import {getDirection} from "../../../utils";
  import _ from "lodash";
  import { validationMixin } from 'vuelidate';
  const { required } = require('vuelidate/lib/validators');
  import {responseMessages} from '../../../constants/response-messages';

  import staticUserTableData from '../../../data/user'
  import {getApiManager} from "../../../api";

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-tree': VTree
    },
    mounted() {
      this.tableData = staticUserTableData;

      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/get-all`).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
            case responseMessages['ok']:
              this.orgList = data;
              break;
            default:

        }
      });

      getApiManager().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.userList = data;
            break;
          default:

        }
      });
    },
    mixins: [validationMixin],
    data() {
      return {
        isLoading: false,
        roleForm: {
          roleName: '',
          note: ''
        },
        roleFlag: '',
        roleFlagData: [
            this.$t('permission-management.permission-control.all'),
            this.$t('permission-management.permission-control.system-management'),
            this.$t('permission-management.permission-control.business-operating'),
            this.$t('permission-management.permission-control.no-role'),
        ],
        dataGroupForm: {
          dataGroupName: '',
          note: '',
        },
        orgList: [],
        userList: [],
        orgUserTreeData: [],
        selectedDataGroup: null,
        isSelectedAllUsersForDataGroup: false,
        dataGroupVuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/permission-control/get-data-group-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: 'dataGroupId',
              title: this.$t('permission-management.permission-control.serial-number'),
              sortField: 'dataGroupId',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'dataGroupName',
              title: this.$t('permission-management.permission-control.data-group-name'),
              // sortField: 'dataGroupName',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:dataGroupFlag',
              title: this.$t('permission-management.permission-control.group-flag'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.permission-control.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'note',
              title: this.$t('permission-management.permission-control.note'),
              // sortField: 'note',
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ],
        },
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
        },
        accessTreeData: [{
          title: 'node1',
          expanded: true,
          children: [{
            title: 'node 1-1',
            expanded: true,
            children: [{
              title: 'node 1-1-1'
            }, {
              title: 'node 1-1-2'
            }, {
              title: 'node 1-1-3'
            }]
          }, {
            title: 'node 1-2',
            children: [{
              title: "<span style='color: red'>node 1-2-1</span>"
            }, {
              title: "<span style='color: red'>node 1-2-2</span>"
            }]
          }]
        }]
      }
    },
    validations: {
      roleForm: {
        roleName: {
          required
        }
      },
      dataGroupForm: {
        dataGroupName: {
          required
        }
      }
    },
    watch: {
      orgList(newVal, oldVal) {
        this.refreshOrgUserTreeData();
      },
      userList(newVal, oldVal) {
        this.refreshOrgUserTreeData();
      },
      selectedDataGroup(newVal, oldVal) {
        if(newVal) {
          let dataGroupUserIds = [];
          newVal.users.forEach((user) => {
            dataGroupUserIds.push(user.userId);
          });
          this.userList.forEach((user) => {
            user.selected = dataGroupUserIds.includes(user.userId);
          });
          this.refreshOrgUserTreeData();
        }
      },
      isSelectedAllUsersForDataGroup(newVal, oldVal) {
          if(this.selectedDataGroup) {
              let tempSelectedDataGroup = this.selectedDataGroup;
              tempSelectedDataGroup.users = newVal ? this.userList : [];
              this.selectedDataGroup = null;
              this.selectedDataGroup = tempSelectedDataGroup;
          }
      }
    },
    methods: {
      onRoleFormSubmit() {
        this.isLoading = true;
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/permission-control/create-role`, {
            'roleName': this.roleForm.roleName,
            'note': this.roleForm.note
          })
          .then((response) => {
            this.isLoading = false;
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-created`), {
                  duration: 3000,
                  permanent: false
                });
                this.roleForm.roleName = '';
                this.roleForm.note = '';
                break;
              default:

            }
          })
          .catch((error) => {
            this.isLoading = false;
          });
      },
      onDataGroupFormSubmit() {
        this.isLoading = true;
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/permission-control/create-data-group`, {
            'dataGroupName': this.dataGroupForm.dataGroupName,
            'note': this.dataGroupForm.note
          })
          .then((response) => {
            this.isLoading = false;
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.data-group-created`), {
                  duration: 3000,
                  permanent: false
                });
                this.dataGroupForm.dataGroupName = '';
                this.dataGroupForm.note = '';
                this.$refs.dataGroupVuetable.reload();
                break;
              default:

            }
          })
          .catch((error) => {
            this.isLoading = false;
          });
        },
      refreshOrgUserTreeData() {
        let pseudoRootId = 0;
        let nest = (orgList, userList, rootId = pseudoRootId) => {
          let childrenOrgList = orgList
            .filter(org => org.parentOrgId === rootId)
            .map(org => ({
              ...org,
              title: org.orgName,
              expanded: true,
              children: nest(orgList, userList, org.orgId)
            }));
          let childrenUserList = userList
            .filter(user => user.orgId === rootId)
            .map(user => ({
              ...user,
              isUser: true,
              title: user.userName,
              expanded: true,
              checked: user.selected,
              children: []
            }));
          return [...childrenOrgList, ...childrenUserList];
        };
        this.orgUserTreeData = nest(this.orgList, this.userList, pseudoRootId);
      },
      dataGroupVuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.dataGroupVuetableItems.perPage,
          filter: {
              dataGroupName: '',
          }
        });
      },
      onDataGroupPaginationData(paginationData) {
        this.$refs.dataGroupPagination.setPaginationData(paginationData)
      },
      onDataGroupRowClass(dataItem, index) {
        let selectedItem = this.selectedDataGroup;
        if(selectedItem && selectedItem.dataGroupId === dataItem.dataGroupId) {
          return 'selected-row';
        } else {
            return '';
        }
      },
      onDataGroupRowClicked(dataItem, event) {
        this.selectedDataGroup = JSON.parse(JSON.stringify(dataItem));
      },
      onPaginationData(paginationData) {
          this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.dataGroupVuetable.changePage(page)
      },
      onAction(action, data, index) {
        console.log('(slot) action: ' + action, data, index)
      },
    }
  }
</script>
