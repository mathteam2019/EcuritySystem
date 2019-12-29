<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('permission-management.permission-control.role-setting')">
        <b-row class="h-100">
          <b-col cols="8" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="3" class="pr-3">
                  <b-form-group>
                    <template slot="label">{{$t('permission-management.permission-control.role')}}</template>
                    <b-form-input v-model="roleKeyword"></b-form-input>
                  </b-form-group>
                </b-col>
                <b-col cols="9" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="searchRoles()">
                      <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="resetRoleSearchForm()">
                      <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('role_export')" @click="onExportRoleButton()">
                      <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('role_print')" @click="onPrintRoleButton()">
                      <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onClickCreateRole()" :disabled="checkPermItem('role_create')" variant="success default">
                      <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                    </b-button>
                  </div>
                </b-col>
              </b-row>

              <b-row class="flex-grow-1 m-0">
                <b-col cols="12">
                  <div class="table-wrapper table-responsive">
                    <vuetable
                      ref="roleVuetable"
                      :fields="roleVuetableItems.fields"
                      :api-url="roleVuetableItems.apiUrl"
                      :http-fetch="roleVuetableHttpFetch"
                      :per-page="roleVuetableItems.perPage"
                      track-by="roleId"
                      pagination-path="data"
                      data-path="data.data"
                      class="table-hover"
                      @vuetable:pagination-data="onRolePaginationData"
                    >
                      <template slot="roleNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onRoleNumberClicked(props.rowData)">
                        {{props.rowData.roleNumber}}
                      </span>
                      </template>
                      <template slot="operating" slot-scope="props">
                        <b-button size="sm" variant="danger default btn-square" class="m-0" :disabled="checkPermItem('role_delete')"
                                  @click="onClickDeleteRole(props.rowData)">
                          <i class="icofont-bin"></i>
                        </b-button>
                      </template>

                    </vuetable>
                  </div>
                  <div class="pagination-wrapper">
                    <vuetable-pagination-bootstrap
                      ref="rolePagination"
                      @vuetable-pagination:change-page="onRoleChangePage"
                      :initial-per-page="roleVuetableItems.perPage"
                      @onUpdatePerPage="roleVuetableItems.perPage = Number($event)"
                    ></vuetable-pagination-bootstrap>
                  </div>
                </b-col>
              </b-row>
            </div>
          </b-col>
          <b-col cols="4" class="pl-0" v-if="selectedRole || roleForm.visible">
            <div class="section d-flex flex-column h-100 px-3">
              <div v-if="roleForm.visible" class="flex-grow-1">
                <b-form @submit.prevent="onRoleFormSubmit" class="h-100 d-flex flex-column">
                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.permission-control.role-number')}}
                      <span class="text-danger">*</span>
                    </template>
                    <b-form-input
                      v-model="roleForm.roleNumber"
                      :state="!$v.roleForm.roleNumber.$invalid"
                      :placeholder="$t('permission-management.permission-control.enter-role-number')"/>
                  </b-form-group>

                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.permission-control.role')}}
                      <span class="text-danger">*</span>
                    </template>
                    <b-form-input
                      v-model="roleForm.roleName"
                      :state="!$v.roleForm.roleName.$invalid"
                      :placeholder="$t('permission-management.permission-control.enter-role-name')"/>
                  </b-form-group>

                  <div>
                    <label class="font-weight-bold">
                      {{$t('permission-management.permission-control.role-flag')}}
                      <span class="text-danger">*</span>
                    </label>
                  </div>

                  <div>
                    <b-form-group>
                      <b-form-radio-group v-model="roleFormFlag">
                        <b-form-radio value="admin">
                          {{$t('permission-management.permission-control.system-management')}}
                        </b-form-radio>
                        <b-form-radio value="user">
                          {{$t('permission-management.permission-control.business-operating')}}
                        </b-form-radio>
                      </b-form-radio-group>
                    </b-form-group>
                  </div>

                  <div class="text-right" v-if="['admin', 'user'].includes(roleFormFlag)">
                    <b-form-group>
                      <b-form-checkbox v-model="isSelectedAllResourcesForRoleForm">
                        {{$t('permission-management.permission-control.select-all')}}
                      </b-form-checkbox>
                    </b-form-group>
                  </div>

                  <div class="flex-grow-1 overflow-auto" style="height: 0px;">
                    <div>
                      <v-tree ref='resourceTreeRoleForm' :data='currentResourceTreeDataForRoleForm' :multiple="true"
                              :halfcheck='true'/>
                    </div>
                  </div>

                  <div class="d-flex align-items-end justify-content-end pt-3">
                    <div>
                      <b-button size="sm" type="submit" variant="info default">
                        <i class="icofont-save"></i>
                        {{ $t('permission-management.permission-control.save') }}
                      </b-button>
                    </div>
                  </div>

                </b-form>
              </div>

              <div v-if="selectedRole" class="flex-grow-1 flex-column d-flex">

                <div>
                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.permission-control.role-number')}}
                      <span class="text-danger">*</span>
                    </template>
                    <label>
                      {{selectedRole.roleNumber}}
                    </label>
                  </b-form-group>
                </div>

                <div>
                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.permission-control.role')}}
                      <span class="text-danger">*</span>
                    </template>
                    <label>
                      {{selectedRole.roleName}}
                    </label>
                  </b-form-group>
                </div>

                <div>
                  <label class="font-weight-bold">
                    {{$t('permission-management.permission-control.role-flag')}}
                    <span class="text-danger">*</span>
                  </label>
                </div>

                <div>
                  <b-form-group>
                    <b-form-radio-group v-model="roleCategory">
                      <b-form-radio value="admin">{{$t('permission-management.permission-control.system-management')}}
                      </b-form-radio>
                      <b-form-radio value="user">{{$t('permission-management.permission-control.user-management')}}
                      </b-form-radio>
                    </b-form-radio-group>
                  </b-form-group>
                </div>

                <div class="text-right" v-if="selectedRole && ['admin', 'user'].includes(selectedRole.roleFlag)">
                  <b-form-group>
                    <b-form-checkbox v-model="isSelectedAllResourcesForRole">
                      {{$t('permission-management.permission-control.select-all')}}
                    </b-form-checkbox>
                  </b-form-group>
                </div>

                <div class="flex-grow-1 overflow-auto" style="height: 0;">
                  <div v-if="selectedRole && ['admin', 'user'].includes(selectedRole.roleFlag)">
                    <v-tree ref='resourceTree' :data='currentResourceTreeData' :multiple="true" :halfcheck='true'/>
                  </div>
                </div>

                <div class="text-right pt-3">
                  <div>
                    <b-button v-if="selectedRole && ['admin', 'user'].includes(selectedRole.roleFlag)" :disabled="checkPermItem('role_modify')"
                              @click="onClickSaveRole" size="sm" variant="info default" class="mr-3">
                      <i class="icofont-save"></i>
                      {{$t('permission-management.permission-control.save')}}
                    </b-button>
                    <b-button @click="onClickDeleteRole(selectedRole)" size="sm" variant="danger default" :disabled="checkPermItem('role_delete')">
                      <i class="icofont-bin"></i>
                      {{$t('permission-management.permission-control.delete')}}
                    </b-button>
                  </div>
                </div>
              </div>
            </div>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.permission-control.data-grouping')">
        <b-row class="h-100">
          <b-col cols="8" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="2" class="pr-3">
                  <b-form-group>
                    <template slot="label">{{$t('permission-management.permission-control.data-group')}}</template>
                    <b-form-input v-model="groupKeyword"></b-form-input>
                  </b-form-group>
                </b-col>

                <b-col cols="2">
                  <b-form-group>
                    <template slot="label">{{$t('permission-management.permission-control.data-range')}}</template>
                    <b-form-input v-model="dataRangeKeyword"></b-form-input>
                  </b-form-group>
                </b-col>

                <b-col cols="8" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="searchDataGroup()">
                      <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="resetDataGroupSearchForm()">
                      <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('data_group_export')" @click="onExportGroupButton()">
                      <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('data_group_print')" @click="onPrintGroupButton()">
                      <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onClickCreateDataGroup()" :disabled="checkPermItem('data_group_create')" variant="success default">
                      <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                    </b-button>
                  </div>
                </b-col>

              </b-row>

              <b-row class="flex-grow-1 m-0">
                <b-col cols="12">
                  <div class="table-wrapper table-responsive">
                    <vuetable
                      ref="dataGroupVuetable"
                      :api-url="dataGroupVuetableItems.apiUrl"
                      :http-fetch="dataGroupVuetableHttpFetch"
                      :fields="dataGroupVuetableItems.fields"
                      :per-page="dataGroupVuetableItems.perPage"
                      pagination-path="data"
                      data-path="data.data"
                      track-by="dataGroupId"
                      class="table-hover"
                      @vuetable:pagination-data="onDataGroupPaginationData"
                    >

                      <template slot="dataGroupNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onDataGroupNumberClicked(props.rowData)">
                        {{props.rowData.dataGroupNumber}}
                      </span>
                      </template>

                      <template slot="operating" slot-scope="props">
                        <b-button size="sm" variant="danger default btn-square" class="m-0"  :disabled="checkPermItem('data_group_delete')"
                                  @click="onClickDeleteDataGroup(props.rowData)">
                          <i class="icofont-bin"></i>
                        </b-button>
                      </template>

                    </vuetable>
                  </div>
                  <div class="pagination-wrapper">
                    <vuetable-pagination-bootstrap
                      ref="dataGroupPagination"
                      @vuetable-pagination:change-page="onDataGroupChangePage"
                      :initial-per-page="dataGroupVuetableItems.perPage"
                      @onUpdatePerPage="dataGroupVuetableItems.perPage = Number($event)"
                    ></vuetable-pagination-bootstrap>
                  </div>
                </b-col>
              </b-row>
            </div>
          </b-col>
          <b-col cols="4" class="pl-0" v-if="selectedDataGroup">
            <div class="section d-flex flex-column h-100 px-3">
              <div v-if="dataGroupDetailStatus === 'create'">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.data-group-number')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="dataGroupForm.dataGroupNumber"
                    :state="!$v.dataGroupForm.dataGroupNumber.$invalid"/>
                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.data-group')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input
                    v-model="dataGroupForm.dataGroupName"
                    :state="!$v.dataGroupForm.dataGroupName.$invalid"/>
                </b-form-group>
              </div>

              <div v-if="dataGroupDetailStatus !== 'create'">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.data-group-number')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <label>
                    {{selectedDataGroup.dataGroupNumber}}
                  </label>
                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.permission-control.data-group-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <label>
                    {{selectedDataGroup.dataGroupName}}
                  </label>
                </b-form-group>
              </div>

              <div>
                <label class="font-weight-bold">{{$t('permission-management.permission-control.data-range')}}<span
                  class="text-danger">*</span></label>
              </div>

              <div class="text-right">
                <b-form-group>
                  <b-form-checkbox v-model="isSelectedAllUsersForDataGroup">
                    {{$t('permission-management.permission-control.select-all')}}
                  </b-form-checkbox>
                </b-form-group>
              </div>

              <div class="flex-grow-1 overflow-auto" style="height: 0;">
                <div>
                  <v-tree ref='orgUserTree' :data='orgUserTreeData' :multiple="true" :halfcheck='true'/>
                </div>
              </div>

              <div class="text-right pt-3" v-if="dataGroupDetailStatus==='create'">
                <div>
                  <b-button @click="createDataGroup()" size="sm" variant="info default"><i class="icofont-save"></i>
                    {{$t('permission-management.permission-control.save')}}
                  </b-button>
                </div>
              </div>

              <div class="text-right pt-3" v-if="dataGroupDetailStatus!=='create'">
                <div>
                  <b-button @click="onClickSaveDataGroup()" size="sm" variant="info default" :disabled="checkPermItem('data_group_modify')"><i
                    class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="onClickDeleteDataGroup" size="sm" variant="danger default" :disabled="checkPermItem('data_group_delete')"><i
                    class="icofont-bin"></i> {{$t('permission-management.permission-control.delete')}}
                  </b-button>
                </div>
              </div>
            </div>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>

    <div v-show="isLoading" class="loading"></div>

    <b-modal centered id="modal-delete-role" ref="modal-delete-role"
             :title="$t('permission-management.permission-control.prompt')">
      {{$t('permission-management.permission-control.delete-role-prompt')}}
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" @click="deleteRole" class="mr-1">{{$t('system-setting.ok')}}</b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-role')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete-data-group" ref="modal-delete-data-group"
             :title="$t('permission-management.permission-control.prompt')">
      {{$t('permission-management.permission-control.delete-data-group-prompt')}}
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" @click="deleteDataGroup" class="mr-1">{{$t('system-setting.ok')}}
        </b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-data-group')">
          {{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

  </div>
</template>

<style>
  .halo-tree .inputCheck {
    top: 2px !important;
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
    color: lightgray !important;
  }

  span.cursor-p {
    cursor: pointer !important;
  }

  .h-35vh {
    height: 32vh;
    max-height: 33vh;
    overflow: auto;
  }
</style>
<style lang="scss">
  .search-form-group {
    [role="group"] {
      position: relative;

      .form-control {
        padding-right: 30px;
      }

      .search-input-icon {
        position: absolute;
        top: 50%;
        right: 1em;
        transform: translateY(-50%);
      }

    }

  }
</style>

<script>

  import {apiBaseUrl} from "../../../constants/config";
  import axios from 'axios'
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import {checkPermissionItem, getDirection, savePermissionInfo} from "../../../utils";
  import _ from "lodash";
  import {validationMixin} from 'vuelidate';

  const {required} = require('vuelidate/lib/validators');
  import {responseMessages} from '../../../constants/response-messages';

  import staticUserTableData from '../../../data/user'
  import {downLoadFileFromServer, getApiManager, printFileFromServer} from "../../../api";

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-tree': VTree
    },
    mounted() {
      this.tableData = staticUserTableData;

      getApiManager().post(`${apiBaseUrl}/permission-management/permission-control/resource/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.resourceList = data;
            break;

          default:

        }
      });

      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgList = data;
            break;
          default:

        }
      });

      getApiManager().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {}).then((response) => {
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
          visible: false,
          roleNumber: '',
          roleName: '',
        },
        roleFormFlag: null,
        isSelectedAllResourcesForRoleForm: false,
        currentResourceTreeDataForRoleForm: [],
        roleKeyword: '',
        resourceList: [],
        resourceTreeData: {
          admin: [],
          user: [],
        },
        currentResourceTreeData: [],
        roleCategory: null,
        selectedRole: null,
        deletingRole: null,
        isSelectedAllResourcesForRole: false,
        roleVuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/permission-control/role/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: 'roleId',
              title: this.$t('permission-management.permission-control.serial-number'),
              // sortField: 'roleId,
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '11%'
            },
            {
              name: '__slot:roleNumber',
              title: this.$t('permission-management.permission-control.role-number'),
              // sortField: 'roleId,
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '29%'
            },
            {
              name: 'roleName',
              title: this.$t('permission-management.permission-control.role-name'),
              // sortField: 'dataGroupName',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '29%'
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.permission-control.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ]
        },
        dataGroupForm: {
          dataGroupNumber: '',
          dataGroupName: '',
          note: '',
        },
        groupKeyword: '',
        dataRangeKeyword: '',
        groupFlag: null,
        orgList: [],
        userList: [],
        orgUserTreeData: [],
        dataGroupDetailStatus: null,
        selectedDataGroup: null,
        deletingDataGroup: null,
        isSelectedAllUsersForDataGroup: false,
        dataGroupVuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/permission-control/data-group/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
                name: '__checkbox',
                titleClass: 'text-center',
                dataClass: 'text-center',
                width: '60px'
            },
            {
              name: 'dataGroupId',
              title: this.$t('permission-management.permission-control.serial-number'),
              // sortField: 'dataGroupId',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '11%'
            },
            {
              name: '__slot:dataGroupNumber',
              title: this.$t('permission-management.permission-control.data-group-number'),
              // sortField: 'dataGroupId',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '21%'
            },
            {
              name: 'dataGroupName',
              title: this.$t('permission-management.permission-control.data-group'),
              // sortField: 'dataGroupName',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '21%'
            },
            {
              name: '__slot:dataGroupRange',
              title: this.$t('permission-management.permission-control.data-group-range'),
              // sortField: 'note',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '18%'
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.permission-control.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
          ],
        },
      }
    },
    validations: {
      roleForm: {
        roleNumber: {
          required
        },
        roleName: {
          required
        }
      },
      dataGroupForm: {
        dataGroupNumber: {
          required
        },
        dataGroupName: {
          required
        }
      }
    },
    watch: {
      'roleVuetableItems.perPage': function (newVal) {
        this.$refs.roleVuetable.refresh();
      },
      'dataGroupVuetableItems.perPage': function (newVal) {
        this.$refs.dataGroupVuetable.refresh();
      },
      resourceList(newVal, oldVal) {
        this.refreshResourceTreeData();
      },
      roleCategory(newVal, oldVal) {
        if (this.selectedRole) {
          this.selectedRole.roleFlag = newVal;
          this.isSelectedAllResourcesForRole = false;
          this.refreshResourceTreeData();
        }
      },
      roleFormFlag(newVal, oldVal) {
        this.isSelectedAllResourcesForRoleForm = false;
        this.refreshResourceTreeData();
      },
      selectedRole(newVal, oldVal) {
        if (newVal) {
          let roleResourceIds = [];
          newVal.resources.forEach((resource) => {
            roleResourceIds.push(resource.resourceId);
          });
          this.resourceList.forEach((resource) => {
            resource.selected = roleResourceIds.includes(resource.resourceId);
          });
          if (newVal.resources.length > 0) {
            this.roleCategory = newVal.resources[0].resourceCategory;
          } else {
            this.roleCategory = null;
          }
          newVal.roleFlag = this.roleCategory;
          this.refreshResourceTreeData();
        }
      },
      isSelectedAllResourcesForRole(newVal, oldVal) {
        if (this.selectedRole) {
          let tempSelectedRole = this.selectedRole;
          tempSelectedRole.resources = newVal ? this.resourceList.filter(resource => resource.resourceCategory === this.selectedRole.roleFlag) : [];
          this.selectedRole = null;
          this.selectedRole = tempSelectedRole;

          this.refreshResourceTreeData();
        }
      },
      isSelectedAllResourcesForRoleForm(newVal, oldVal) {
        this.resourceList.forEach((resource) => {
          resource.selected = newVal && (resource.resourceCategory === this.roleFormFlag);
        });
        this.refreshResourceTreeData();
      },
      orgList(newVal, oldVal) {
        this.refreshOrgUserTreeData();
      },
      userList(newVal, oldVal) {
        this.refreshOrgUserTreeData();
      },
      selectedDataGroup(newVal, oldVal) {
        if (newVal) {
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
        if (this.selectedDataGroup) {
          let tempSelectedDataGroup = this.selectedDataGroup;
          tempSelectedDataGroup.users = newVal ? this.userList : [];
          this.selectedDataGroup = null;
          this.selectedDataGroup = tempSelectedDataGroup;
        }
      }
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportRoleButton() {
        let checkedAll = this.$refs.roleVuetable.checkedAllStatus;
        let checkedIds = this.$refs.roleVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {roleName : this.roleKeyword},
          'idList': checkedIds.join()
        };
        let link = `permission-management/permission-control/role`;
        downLoadFileFromServer(link, params, 'permission-role');
      },
      onPrintRoleButton() {
        let checkedAll = this.$refs.roleVuetable.checkedAllStatus;
        let checkedIds = this.$refs.roleVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {roleName : this.roleKeyword},
          'idList': checkedIds.join()
        };
        let link = `permission-management/permission-control/role`;
        printFileFromServer(link, params);
      },
      onExportGroupButton() {
        let checkedAll = this.$refs.dataGroupVuetable.checkedAllStatus;
        let checkedIds = this.$refs.dataGroupVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {dataGroupName : this.dataRangeKeyword},
          'idList': checkedIds.join()
        };
        let link = `permission-management/permission-control/data-group`;
        downLoadFileFromServer(link, params, 'permission-dataGroup');
      },
      onPrintGroupButton() {
        let checkedAll = this.$refs.dataGroupVuetable.checkedAllStatus;
        let checkedIds = this.$refs.dataGroupVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {dataGroupName : this.dataRangeKeyword},
          'idList': checkedIds.join()
        };
        let link = `permission-management/permission-control/data-group`;
        printFileFromServer(link, params);
      },

      hideModal(refName) {
        this.$refs[refName].hide();
      },

      onRoleFormSubmit() {
        if (this.$v.roleForm.$invalid) {
          return;
        }
        this.isLoading = true;
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/permission-control/role/create`, {
            'roleNumber': this.roleForm.roleNumber,
            'roleName': this.roleForm.roleName,
            'resourceIdList': this.$refs.resourceTreeRoleForm ? this.$refs.resourceTreeRoleForm.getCheckedNodes().map(node => node.resourceId) : [],
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
                this.$refs.roleVuetable.reload();
                this.roleForm.roleNumber = '';
                this.roleForm.roleName = '';
                this.roleFormFlag = null;
                break;
              case responseMessages['used-role-name']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-role-name`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-role-number']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-role-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              default:


            }
          })
          .catch((error) => {
            this.isLoading = false;
          });
      },
      searchRoles() {
        this.$refs.roleVuetable.refresh();
      },
      resetRoleSearchForm() {
        this.roleKeyword = '';
      },
      onClickCreateRole() {
        this.selectedRole = null;
        this.roleForm.visible = true;
        this.roleFormFlag = null;
        this.currentResourceTreeDataForRoleForm = [];
        this.resourceList.forEach((resource) => {
          resource.selected = false;
        });
      },
      onClickSaveRole() {
        if (this.selectedRole) {
          this.isLoading = true;
          let checkedNodes = this.$refs.resourceTree.getCheckedNodes();
          let roleResourceIds = checkedNodes.map(node => node.resourceId);
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/permission-control/role/modify`, {
              'roleId': this.selectedRole.roleId,
              'resourceIdList': roleResourceIds
            })
            .then((response) => {
              this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-modified`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.roleVuetable.reload();
                  savePermissionInfo(response.data.data.permission);
                  break;
                case responseMessages['used-role-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-role-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-role-number']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-role-number`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              this.isLoading = false;
            });
        }
      },
      onClickDeleteRole(role) {
        this.deletingRole = role;
        this.$refs['modal-delete-role'].show();
      },
      deleteRole() {
        if (this.deletingRole) {
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/permission-control/role/delete`, {
              'roleId': this.deletingRole.roleId
            })
            .then((response) => {
              this.isLoading = false;
              this.hideModal('modal-delete-role')
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-deleted`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.deletingRole = null;
                  this.$refs.roleVuetable.refresh();
                  savePermissionInfo(response.data.data.permission);
                  break;
                case responseMessages['has-resources']:
                  this.$notify('error', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-users']:
                case responseMessages['has-user-groups']:
                  this.$notify('error', this.$t('permission-management.warning'), this.$t(`response-error-message.role-already-used`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              this.isLoading = false;
            });
        }
      },
      refreshResourceTreeData() {
        let pseudoRootId = 0;
        let nest = (resourceList, rootId = pseudoRootId) =>
          resourceList
            .filter(resource => resource.parentResourceId === rootId)
            .map(resource => ({
              ...resource,
              title: resource.resourceCaption,
              expanded: true,
              children: nest(resourceList, resource.resourceId),
              checked: resource.selected
            }));
        let resourceTreeData = nest(this.resourceList, pseudoRootId);
        this.resourceTreeData.admin = resourceTreeData.filter(rootNode => rootNode.resourceCategory === 'admin');
        this.resourceTreeData.user = resourceTreeData.filter(rootNode => rootNode.resourceCategory === 'user');
        if (this.selectedRole) {
          if (this.selectedRole.roleFlag === 'admin') {
            this.currentResourceTreeData = this.resourceTreeData.admin;
          } else if (this.selectedRole.roleFlag === 'user') {
            this.currentResourceTreeData = this.resourceTreeData.user;
          } else {
            this.currentResourceTreeData = [];
          }
        }
        if (this.roleFormFlag === 'admin') {
          this.currentResourceTreeDataForRoleForm = this.resourceTreeData.admin;
        } else if (this.roleFormFlag === 'user') {
          this.currentResourceTreeDataForRoleForm = this.resourceTreeData.user;
        } else {
          this.currentResourceTreeDataForRoleForm = [];
        }
      },
      roleVuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.roleVuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            roleName: this.roleKeyword,
          }
        });
      },
      onRolePaginationData(paginationData) {
        this.$refs.rolePagination.setPaginationData(paginationData)
      },
      onRoleNumberClicked(dataItem) {
        this.roleForm.visible = false;
        this.selectedRole = JSON.parse(JSON.stringify(dataItem));
      },
      onRoleChangePage(page) {
        this.$refs.roleVuetable.changePage(page);
      },

      onClickCreateDataGroup() {
        this.selectedDataGroup = {
          users: []
        };
        this.dataGroupForm.dataGroupName = '';
        this.dataGroupForm.dataGroupNumber = '';
        this.dataGroupDetailStatus = 'create';
      },

      createDataGroup() {

        if (this.$v.dataGroupForm.$invalid) {
          return;
        }

        if (this.selectedDataGroup) {
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let userIdList = checkedNodes.filter(node => node.isUser).map(node => node.userId);
          this.isLoading = true;
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/permission-control/data-group/create`, {
              'dataGroupNumber': this.dataGroupForm.dataGroupNumber,
              'dataGroupName': this.dataGroupForm.dataGroupName,
              'userIdList': userIdList
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
                  this.dataGroupForm.dataGroupNumber = '';
                  this.$refs.dataGroupVuetable.refresh();
                  break;
                case responseMessages['used-data-group-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-data-group-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-data-group-number']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-data-group-number`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;

                default:

              }
            })
            .catch((error) => {
              this.isLoading = false;
            });
        }
      },
      onClickSaveDataGroup() {
        if (this.selectedDataGroup) {
          this.isLoading = true;
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let dataGroupUserIds = [];
          checkedNodes.forEach((node) => {
            if (node.isUser) dataGroupUserIds.push(node.userId);
          });
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/permission-control/data-group/modify`, {
              'dataGroupId': this.selectedDataGroup.dataGroupId,
              'userIdList': dataGroupUserIds
            })
            .then((response) => {
              this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.data-group-modified`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.dataGroupVuetable.reload();
                  break;
                case responseMessages['used-data-group-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-data-group-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-data-group-number']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-data-group-number`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-children']:
                  this.$notify('error', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-users']:
                case responseMessages['has-user-groups']:
                  this.$notify('error', this.$t('permission-management.warning'), this.$t(`response-error-message.data-group-already-used`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              this.isLoading = false;
            });
        }
      },
      onClickDeleteDataGroup(dataGroup) {
        this.deletingDataGroup = dataGroup;
        this.$refs['modal-delete-data-group'].show();
      },
      deleteDataGroup() {
        if (this.deletingDataGroup) {
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/permission-control/data-group/delete`, {
              'dataGroupId': this.deletingDataGroup.dataGroupId
            })
            .then((response) => {
              this.hideModal('modal-delete-data-group');
              this.isLoading = false;
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.deletingDataGroup = null;
                  this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.data-group-deleted`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.dataGroupVuetable.refresh();
                  break;
                case responseMessages['has-children']:
                  this.$notify('error', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-users']:
                case responseMessages['has-user-groups']:
                  this.$notify('error', this.$t('permission-management.warning'), this.$t(`response-error-message.data-group-already-used`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                default:

              }
            })
            .catch((error) => {
              this.isLoading = false;
            });
        }
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
      searchDataGroup() {
        this.$refs.dataGroupVuetable.refresh();
      },
      resetDataGroupSearchForm() {
        this.groupKeyword = '';
        this.dataRangeKeyword = '';
      },
      dataGroupVuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.dataGroupVuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            dataGroupName: this.groupKeyword,
          }
        });
      },
      onDataGroupPaginationData(paginationData) {
        this.$refs.dataGroupPagination.setPaginationData(paginationData)
      },
      onDataGroupRowClass(dataItem, index) {
        let selectedItem = this.selectedDataGroup;
        if (selectedItem && selectedItem.dataGroupId === dataItem.dataGroupId) {
          return 'selected-row';
        } else {
          return '';
        }
      },
      onDataGroupNumberClicked(dataItem) {
        this.dataGroupDetailStatus = 'modify';
        this.selectedDataGroup = JSON.parse(JSON.stringify(dataItem));
      },
      onDataGroupChangePage(page) {
        this.$refs.dataGroupVuetable.changePage(page)
      },
    }
  }
</script>
