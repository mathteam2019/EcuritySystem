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

      <b-tab :title="$t('permission-management.permission-control.role-setting')" @click="tabStatus = 'role'">
        <b-row class="h-100">
          <b-col cols="8" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="3" class="pr-3">
                  <b-form-group>
                    <template slot="label">{{$t('permission-management.permission-control.role')}}</template>
                    <b-form-input v-model="roleKeyword"/>
                  </b-form-group>
                </b-col>
                <b-col cols="9" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="searchRoles()">
                      <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="resetRoleSearchForm()">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default"
                              :disabled="checkPermItem('role_export')" @click="onExportButton()">
                      <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default"
                              :disabled="checkPermItem('role_print')" @click="onPrintRoleButton()">
                      <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onClickCreateRole()"
                              :disabled="checkPermItem('role_create')" variant="success default">
                      <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
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
                      pagination-path="rolePagination"
                      class="table-hover"
                      @vuetable:checkbox-toggled="onCheckStatusChange"
                      @vuetable:pagination-data="onRolePaginationData"
                    >
                      <template slot="roleNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onRoleNumberClicked(props.rowData)">
                        {{props.rowData.roleNumber}}
                      </span>
                      </template>
                      <template slot="operating" slot-scope="props">
                        <b-button size="sm" variant="danger default btn-square" class="m-0"
                                  :disabled="checkPermItem('role_delete')"
                                  @click="onClickDeleteRole(props.rowData)">
                          <i class="icofont-bin"/>
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
                    />
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

                  <div class="text-left">
                    <b-form-group>
                      <b-form-checkbox v-model="isSelectedAllResourcesForRoleForm">
                        {{$t('permission-management.permission-control.select-all')}}
                      </b-form-checkbox>
                    </b-form-group>
                  </div>

                  <div class="flex-grow-1 overflow-auto" style="height: 0;">
                    <div>
                      <v-tree ref='resourceTreeRoleForm' :data='resourceTreeData' :multiple="true"
                              :halfcheck='true'/>
                    </div>
                  </div>

                  <div class="d-flex align-items-end justify-content-end pt-3">
                    <div>
                      <b-button size="sm" type="submit" variant="info default">
                        <i class="icofont-save"/>
                        {{ $t('permission-management.permission-control.save') }}
                      </b-button>
                      <b-button @click="roleForm.visible = false"
                                variant="danger default" size="sm"><i
                        class="icofont-long-arrow-left"/> {{$t('system-setting.cancel')}}
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

                <div class="text-left" v-if="selectedRole">
                  <b-form-group>
                    <b-form-checkbox v-model="isSelectedAllResourcesForRole">
                      {{$t('permission-management.permission-control.select-all')}}
                    </b-form-checkbox>
                  </b-form-group>
                </div>

                <div class="flex-grow-1 overflow-auto" style="height: 0;">
                  <div v-if="selectedRole">
                    <v-tree ref='resourceTree' :data='resourceTreeData' :multiple="true" :halfcheck='true'/>
                  </div>
                </div>

                <div class="text-right pt-3">
                  <div>
                    <b-button v-if="selectedRole" :disabled="checkPermItem('role_modify')"
                              @click="onClickSaveRole" size="sm" variant="info default" class="mr-3">
                      <i class="icofont-save"/>
                      {{$t('permission-management.permission-control.save')}}
                    </b-button>

                    <b-button @click="onClickDeleteRole(selectedRole)" size="sm" variant="danger default"
                              :disabled="checkPermItem('role_delete')">
                      <i class="icofont-bin"/>
                      {{$t('permission-management.permission-control.delete')}}
                    </b-button>
                  </div>
                </div>
              </div>
            </div>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.permission-control.data-grouping')" @click="tabStatus = 'group'">
        <b-row class="h-100">
          <b-col cols="8" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="2" class="pr-3">
                  <b-form-group>
                    <template slot="label">{{$t('permission-management.permission-control.data-group')}}</template>
                    <b-form-input v-model="groupKeyword"/>
                  </b-form-group>
                </b-col>

                <b-col cols="2">
                  <b-form-group>
                    <template slot="label">{{$t('permission-management.permission-control.data-range')}}</template>
                    <b-form-input v-model="userName"/>
                  </b-form-group>
                </b-col>

                <b-col cols="8" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="searchDataGroup()">
                      <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="resetDataGroupSearchForm()">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default"
                              :disabled="checkPermItem('data_group_export')" @click="onExportButton()">
                      <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default"
                              :disabled="checkPermItem('data_group_print')" @click="onPrintGroupButton()">
                      <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onClickCreateDataGroup()"
                              :disabled="checkPermItem('data_group_create')" variant="success default">
                      <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
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
                      pagination-path="dataGroupPagination"
                      track-by="dataGroupId"
                      class="table-hover"
                      @vuetable:checkbox-toggled="onCheckStatusChangeGroup"
                      @vuetable:pagination-data="onDataGroupPaginationData"
                    >

                      <template slot="dataGroupNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onDataGroupNumberClicked(props.rowData)">
                        {{props.rowData.dataGroupNumber}}
                      </span>
                      </template>

                      <template slot="operating" slot-scope="props">
                        <b-button size="sm" variant="danger default btn-square" class="m-0"
                                  :disabled="checkPermItem('data_group_delete')"
                                  @click="onClickDeleteDataGroup(props.rowData)">
                          <i class="icofont-bin"/>
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
                    />
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

              <div class="text-left">
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
                  <b-button @click="createDataGroup()" size="sm" variant="info default"><i class="icofont-save"/>
                    {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="selectedDataGroup = false"
                            variant="danger default" size="sm"><i
                    class="icofont-long-arrow-left"/> {{$t('system-setting.cancel')}}
                  </b-button>
                </div>
              </div>

              <div class="text-right pt-3" v-if="dataGroupDetailStatus!=='create'">
                <div>
                  <b-button @click="onClickSaveDataGroup()" size="sm" variant="info default"
                            :disabled="checkPermItem('data_group_modify')"><i
                    class="icofont-save"/> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="onClickDeleteDataGroup(selectedDataGroup)" size="sm" variant="danger default"
                            :disabled="checkPermItem('data_group_delete')"><i
                    class="icofont-bin"/> {{$t('permission-management.permission-control.delete')}}
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
      {{$t('permission-management.user.user-group-delete-prompt')}}
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" @click="deleteRole" class="mr-1">{{$t('system-setting.ok')}}</b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-role')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete-data-group" ref="modal-delete-data-group"
             :title="$t('permission-management.permission-control.prompt')">
      {{$t('permission-management.user.user-group-delete-prompt')}}
      <template slot="modal-footer">
        <b-button size="sm" variant="primary" @click="deleteDataGroup" class="mr-1">{{$t('system-setting.ok')}}
        </b-button>
        <b-button size="sm" variant="danger" @click="hideModal('modal-delete-data-group')">
          {{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export')
            }}</h3>
        </b-col>
      </b-row>
      <b-row style="height : 100px;">
        <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
          <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid" :searchable="false"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </b-col>
      </b-row>
      <div slot="modal-footer">
        <b-button size="sm" variant="orange default" @click="onExportButtonModel()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="hideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
    <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />

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
  import Modal from '../../../components/Modal/modal'

  const {required} = require('vuelidate/lib/validators');
  import {responseMessages} from '../../../constants/response-messages';

  import staticUserTableData from '../../../data/user'
  import {
    getApiManager,
    getApiManagerError,
    printFileFromServer,
    isRoleNumberValid,
    isSpaceContain,
    isDataGroupNumberValid
  } from "../../../api";

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-tree': VTree,
      Modal
    },
    mounted() {
      this.$refs.roleVuetable.$parent.transform = this.transform.bind(this);
      this.$refs.dataGroupVuetable.$parent.transform = this.fnTransformUserGroupTable.bind(this);
      this.tableData = staticUserTableData;

      getApiManagerError().post(`${apiBaseUrl}/permission-management/permission-control/resource/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.resourceList = data;
            break;

          default:

        }
      });

      getApiManagerError().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgList = data;
            break;
          default:

        }
      });

      getApiManagerError().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {}).then((response) => {
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
        direction: getDirection().direction,
        roleForm: {
          visible: false,
          roleNumber: '',
          roleName: '',
        },
        tabStatus: 'role',
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        isModalVisible: false,
        isSelectedAllResourcesForRoleForm: false,
        currentResourceTreeDataForRoleForm: [],
        roleKeyword: '',
        resourceList: [],
        resourceTreeData: [],
        renderedCheckList: [],
        renderedCheckListGroup: [],
        selectedRole: null,
        deletingRole: null,
        isSelectedAllResourcesForRole: false,
        roleVuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/permission-control/role/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.permission-control.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '11%'
            },
            {
              name: '__slot:roleNumber',
              title: this.$t('permission-management.permission-control.role-number'),
              sortField: 'roleNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '29%'
            },
            {
              name: 'roleName',
              title: this.$t('permission-management.permission-control.role-name'),
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
        userName :'',
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
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.permission-control.serial-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '11%'
            },
            {
              name: '__slot:dataGroupNumber',
              title: this.$t('permission-management.permission-control.data-group-number'),
              sortField: 'dataGroupNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '21%'
            },
            {
              name: 'dataGroupName',
              title: this.$t('permission-management.permission-control.data-group'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '21%'
            },
            {
              name: 'groupMember',
              title: this.$t('permission-management.permission-control.data-group-range'),
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
      fileSelection: {
        required
      },
      roleForm: {
        roleNumber: {
          required,
          isRoleNumberValid
        },
        roleName: {
          required,
          isSpaceContain
        }
      },
      dataGroupForm: {
        dataGroupNumber: {
          required,
          isDataGroupNumberValid
        },
        dataGroupName: {
          required
        }
      }
    },
    watch: {
      'roleVuetableItems.perPage': function (newVal) {
        this.$refs.roleVuetable.refresh();
        this.changeCheckAllStatus();
      },
      'dataGroupVuetableItems.perPage': function (newVal) {
        this.$refs.dataGroupVuetable.refresh();
        this.changeCheckAllStatusGroup();
      },
      resourceList(newVal, oldVal) {
        this.refreshResourceTreeData();
      },
      selectedRole(newVal, oldVal) {
        if (newVal) {
          if(newVal.resources.length === this.resourceList.length) {
            this.isSelectedAllResourcesForRole = true;
          }
          else {
            this.isSelectedAllResourcesForRole = false;
          }
          console.log(newVal.resources.length === this.resourceList.length);
          let roleResourceIds = [];
          newVal.resources.forEach((resource) => {
            roleResourceIds.push(resource.resourceId);
          });
          this.resourceList.forEach((resource) => {
            resource.selected = roleResourceIds.includes(resource.resourceId);
          });
          this.refreshResourceTreeData();
        }
      },
      isSelectedAllResourcesForRole(newVal, oldVal) {
        if (this.selectedRole) {
          let tempSelectedRole = this.selectedRole;
          tempSelectedRole.resources = newVal ? this.resourceList : [];
          this.selectedRole = null;
          this.selectedRole = tempSelectedRole;

          this.refreshResourceTreeData();
        }
      },
      isSelectedAllResourcesForRoleForm(newVal, oldVal) {
        this.resourceList.forEach((resource) => {
          resource.selected = newVal;
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
          if(newVal.users.length === this.userList.length) {
            this.isSelectedAllResourcesForRoleForm = true;
          }
          else {
            this.isSelectedAllResourcesForRoleForm = false;
          }
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
          this.refreshOrgUserTreeData();
        }
      }
    },
    methods: {
      selectAll(value){
        this.$refs.roleVuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.roleVuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.roleVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;

      },
      selectNone(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.roleVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;

      },
      changeCheckAllStatus(){
        let selectList = this.$refs.roleVuetable.selectedTo;
        let renderedList = this.renderedCheckList;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNone();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAll(true);
            }
          }
        }
        else {
          this.selectNone();
        }

      },
      selectAllGroup(value){
        this.$refs.dataGroupVuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.dataGroupVuetable.isCheckAllStatus=value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.dataGroupVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneGroup(){
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.dataGroupVuetable.uuid;
        let checkAllButton =  document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusGroup(){
        let selectList = this.$refs.dataGroupVuetable.selectedTo;
        let renderedList = this.renderedCheckListGroup;
        if(selectList.length>=renderedList.length){
          let isEqual = false;
          for(let i=0; i<renderedList.length; i++){
            isEqual = false;
            for(let j=0; j<selectList.length; j++){
              if(renderedList[i]===selectList[j]) {j=selectList.length; isEqual=true}
            }
            if(isEqual===false){
              this.selectNoneGroup();
              break;
            }
            if(i===renderedList.length-1){
              this.selectAllGroup(true);
            }
          }
        }
        else {
          this.selectNoneGroup();
        }

      },
      onCheckStatusChange(isChecked){
        if(isChecked){
          this.changeCheckAllStatus();
        }
        else {
          this.selectNone();
        }
      },
      onCheckStatusChangeGroup(isChecked){
        if(isChecked){
          this.changeCheckAllStatusGroup();
        }
        else {
          this.selectNoneGroup();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        if (this.tabStatus === 'role') {
          this.onExportRole();
        }
        if (this.tabStatus === 'group') {
          this.onExportGroup();
        }
        this.isModalVisible = true;
      },
      onExportButtonModel() {
        if (this.tabStatus === 'role') {
          this.onExportRole();
        }
        if (this.tabStatus === 'group') {
          this.onExportGroup();
        }
      },
      onExportRole() {
        let checkedAll = this.$refs.roleVuetable.checkedAllStatus;
        let checkedIds = this.$refs.roleVuetable.selectedTo;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {roleName: this.roleKeyword},
          'idList': checkedIds.join()
        };
        this.link = `permission-management/permission-control/role`;
        this.name = 'role';
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'role', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },
      onPrintRoleButton() {
        let checkedAll = this.$refs.roleVuetable.checkedAllStatus;
        let checkedIds = this.$refs.roleVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {roleName: this.roleKeyword},
          'idList': checkedIds.join()
        };
        let link = `permission-management/permission-control/role`;
        printFileFromServer(link, params);
      },
      onExportGroup() {
        let checkedAll = this.$refs.dataGroupVuetable.checkedAllStatus;
        let checkedIds = this.$refs.dataGroupVuetable.selectedTo;
        this.params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {dataGroupName: this.groupKeyword,
                      userName: this.userName},
          'idList': checkedIds.join()
        };
        this.link = `permission-management/permission-control/data-group`;
        this.name = 'Permission-DataGroup';
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'Permission-DataGroup', this.fileSelection);
        //   this.hideModal('model-export')
        // }
      },
      onPrintGroupButton() {
        let checkedAll = this.$refs.dataGroupVuetable.checkedAllStatus;
        let checkedIds = this.$refs.dataGroupVuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': {dataGroupName: this.groupKeyword,
                      userName:this.userName},
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
          if (this.$v.roleForm.roleNumber.$invalid) {
            if(this.roleForm.roleNumber==='') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.enter-role-number`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.enter-role-number-invalid`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.roleForm.roleName.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.enter-role-name`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }

        let resourceIdList = this.$refs.resourceTreeRoleForm ? this.$refs.resourceTreeRoleForm.getCheckedNodes().map(node => node.resourceId) : [];
        if (resourceIdList.length === 0) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.required-role`), {
            duration: 3000,
            permanent: false
          });
        } else {
          this.isLoading = true;
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/permission-control/role/create`, {
              'roleNumber': this.roleForm.roleNumber,
              'roleName': this.roleForm.roleName,
              'resourceIdList': resourceIdList
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
                  this.$refs.roleVuetable.refresh();
                  this.roleForm.roleNumber = '';
                  this.roleForm.roleName = '';
                  this.roleForm.visible = false;

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
      searchRoles() {
        this.$refs.roleVuetable.refresh();
      },
      resetRoleSearchForm() {
        this.roleKeyword = '';
      },
      onClickCreateRole() {
        this.selectedRole = null;
        this.isSelectedAllResourcesForRoleForm = false;
        let roleNumberStr = "R";
        for (let i = 0; i < 8; i++) {
          let index = Math.floor(Math.random() * 10);
          roleNumberStr = roleNumberStr + index.toString();
        }
        this.roleForm.visible = true;
        this.roleForm.roleNumber = roleNumberStr;
        this.currentResourceTreeDataForRoleForm = [];
        this.resourceList.forEach((resource) => {
          resource.selected = false;
        });
      },
      onClickSaveRole() {
        if (this.selectedRole) {

          let checkedNodes = this.$refs.resourceTree.getCheckedNodes();
          let roleResourceIds = checkedNodes.map(node => node.resourceId);
          if (roleResourceIds.length === 0) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.required-role`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          this.isLoading = true;
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

                  this.selectedRole = null;
                  savePermissionInfo(response.data.data.permission);
                  //this.roleForm.visible = false;
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

                  this.selectedRole = null;
                  savePermissionInfo(response.data.data.permission);
                  //this.roleForm.visible = false;
                  break;
                case responseMessages['has-resources']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-users']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.role-already-used`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-user-groups']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.role-already-used`), {
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
        this.resourceTreeData = nest(this.resourceList, pseudoRootId);

      },
      roleVuetableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckList =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.roleVuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            roleName: this.roleKeyword,
          }
        });
      },
      transform(response) {

        let transformed = {};

        let data = response.data;

        transformed.rolePagination = {
          total: data.total,
          per_page: data.per_page,
          current_page: data.current_page,
          last_page: data.last_page,
          from: data.from,
          to: data.to
        };

        transformed.data = [];
        let temp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          transformed.data.push(temp);
          this.renderedCheckList.push(data.data[i].roleId);
        }

        return transformed

      },
      onRolePaginationData(paginationData) {
        this.$refs.rolePagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onRoleNumberClicked(dataItem) {
        this.roleForm.visible = false;
        this.selectedRole = JSON.parse(JSON.stringify(dataItem));
      },
      onRoleChangePage(page) {
        this.$refs.roleVuetable.changePage(page);
        this.changeCheckAllStatus();
      },

      onClickCreateDataGroup() {
        this.isSelectedAllUsersForDataGroup =false;
        this.selectedDataGroup = {
          users: []
        };
        let dataGroupNumberStr = "DG";
        for (let i = 0; i < 8; i++) {
          let index = Math.floor(Math.random() * 10);
          dataGroupNumberStr = dataGroupNumberStr + index.toString();
        }
        this.dataGroupForm.dataGroupName = '';
        this.dataGroupForm.dataGroupNumber = dataGroupNumberStr;
        this.dataGroupDetailStatus = 'create';
      },

      createDataGroup() {

        if (this.$v.dataGroupForm.$invalid) {
          if (this.$v.dataGroupForm.dataGroupNumber.$invalid) {
            if(this.dataGroupForm.dataGroupNumber==='') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.enter-data-group-number`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.enter-data-group-number-invalid`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.dataGroupForm.dataGroupName.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.enter-data-group-name`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          return;
        }

        if (this.selectedDataGroup) {
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let userIdList = checkedNodes.filter(node => node.isUser).map(node => node.userId);
          if (userIdList.length === 0) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.required-data-group`), {
              duration: 3000,
              permanent: false
            });
          } else {
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
                    this.selectedDataGroup = false;
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

          if(dataGroupUserIds.length === 0) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.permission-control.required-data-group`), {
              duration: 3000,
              permanent: false
            });
          }
          if(dataGroupUserIds.length !== 0) {
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
                    this.selectedDataGroup = false;
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
                    this.$notify('error', this.$t('permission-management.warning'), this.$t(`permission-management.user.data-group-has-users`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;
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
                  this.selectedDataGroup = false;
                  break;
                case responseMessages['has-children']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.data-group-has-child`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-users']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.data-group-has-users`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-user-groups']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.data-group-already-used`), {
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

        this.getTreeData(this.orgUserTreeData, 0);
      },
      getTreeData(treeData, index) {
        var str = "";
        for(var i = 0; i < index * 2; i ++) str = str + "-";

        if(!treeData || treeData.length===0){
          return ;
        }
        let tmp = treeData;
        var answer = [];
        for(let i=tmp.length - 1; i >= 0; i--){

          if(tmp[i].userId != null && tmp[i].userId != undefined) {
            continue;
          }
          this.getTreeData(tmp[i].children, index + 1);
          if(!tmp[i].children || tmp[i].children.length == 0) {
            tmp.splice(i, 1);

          }
        }
        return;


        //
        // let array = [];
        // for (let b in a.children) {
        //   if(this.isInvalid(b)) array.push(b)
        // }
        // a.children = array;
        // //array.size = 0
        // return a;
      },
      searchDataGroup() {
        this.$refs.dataGroupVuetable.refresh();
      },
      resetDataGroupSearchForm() {
        this.groupKeyword = '';
        this.userName= '';
      },
      dataGroupVuetableHttpFetch(apiUrl, httpOptions) {
        this.renderedCheckListGroup =[];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.dataGroupVuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            dataGroupName: this.groupKeyword,
            userName:this.userName
          }
        });
      },
      fnTransformUserGroupTable(response) {
        let transformed = {};

        let data = response.data;

        transformed.dataGroupPagination = {
          total: data.total,
          per_page: data.per_page,
          current_page: data.current_page,
          last_page: data.last_page,
          from: data.from,
          to: data.to
        };

        transformed.data = [];
        let temp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          transformed.data.push(temp);
          let usersName =[];
          temp.users.forEach(users => {
            usersName.push(users.userName);
          });
          let groupMember = usersName.join(',');
          if(groupMember.length>30){
            groupMember = groupMember.substr(0, 30) + "···"; // Gets the first part
          }
          temp.groupMember = groupMember;
          this.renderedCheckListGroup.push(data.data[i].dataGroupId);
        }

        return transformed

      },
      onDataGroupPaginationData(paginationData) {
        this.$refs.dataGroupPagination.setPaginationData(paginationData);
        this.changeCheckAllStatusGroup();
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
        this.$refs.dataGroupVuetable.changePage(page);
        this.changeCheckAllStatusGroup();
      },
    }
  }
</script>
