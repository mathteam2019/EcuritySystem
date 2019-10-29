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
                    <b-form-select v-model="roleFlag" @change="onRoleFlagChanged" :options="roleFlagData" plain/>
                  </b-form-group>
                </b-col>

                <b-col cols="7">
                  <b-form-group>
                    <template slot="label">&nbsp;</template>
                    <b-form-input v-model="roleKeyword" @change="onRoleKeywordChanged"></b-form-input>
                  </b-form-group>
                </b-col>
              </b-row>

              <b-row>
                <b-col cols="12">
                  <vuetable
                    ref="roleVuetable"
                    :fields="roleVuetableItems.fields"
                    :api-url="roleVuetableItems.apiUrl"
                    :http-fetch="roleVuetableHttpFetch"
                    :per-page="roleVuetableItems.perPage"
                    pagination-path="data"
                    data-path="data.data"
                    :row-class="onRoleRowClass"
                    @vuetable:pagination-data="onRolePaginationData"
                    @vuetable:row-clicked="onRoleRowClicked"
                  >
                    <template slot="roleFlag" slot-scope="props">
                      <div v-if="props.rowData.roleFlag === 'admin'" class="glyph-icon iconsminds-gear text-info tb-icon"></div>
                      <div v-else-if="props.rowData.roleFlag === 'user'" class="glyph-icon iconsminds-file-edit text-info tb-icon"></div>
                    </template>

                    <template slot="operating" slot-scope="props">
                      <div v-if="!['admin', 'user'].includes(props.rowData.roleFlag)" @click="onClickDeleteRole(props.rowData)" class="glyph-icon simple-icon-close text-danger tb-button"></div>
                      <div v-else class="glyph-icon simple-icon-close tb-button-disabled"></div>
                    </template>

                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="rolePagination"
                    @vuetable-pagination:change-page="onRoleChangePage"
                    :initial-per-page="roleVuetableItems.perPage"
                    @onUpdatePerPage="roleVuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
          <b-col cols="4">
            <b-card class="mb-4" v-if="selectedRole">

              <b-row>
                <b-form-group>
                  <b-form-radio-group>
                    <b-form-radio v-model="roleCategory" value="admin">{{$t('permission-management.permission-control.system-management')}}</b-form-radio>
                    <b-form-radio v-model="roleCategory" value="user">{{$t('permission-management.permission-control.business-operating')}}</b-form-radio>
                  </b-form-radio-group>
                </b-form-group>
              </b-row>

              <b-row v-if="selectedRole && ['admin', 'user'].includes(selectedRole.roleFlag)">
                <b-col cols="12" class="text-right">
                  <b-form-group>
                    <b-form-checkbox v-model="isSelectedAllResourcesForRole">{{$t('permission-management.permission-control.select-all')}}</b-form-checkbox>
                  </b-form-group>
                </b-col>
                <b-col cols="12">
                  <v-tree ref='resourceTree' :data='currentResourceTreeData' :multiple="true" :halfcheck='true' />
                </b-col>
                <b-col cols="12" class="text-right">
                  <b-form-group>
                    <b-button @click="onClickSaveRole">{{$t('permission-management.permission-control.save')}}</b-button>
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
                  <b-form-group :label="$t('permission-management.permission-control.group-flag')">
                    <b-form-select v-model="groupFlag" @change="onGroupFlagChanged" :options="groupFlagData" plain/>
                  </b-form-group>
                </b-col>

                <b-col cols="7">
                  <b-form-group>
                    <template slot="label">&nbsp;</template>
                    <b-form-input v-model="groupKeyword" @change="onGroupKeywordChanged"></b-form-input>
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
                      <div v-if="props.rowData.users.length" class="glyph-icon iconsminds-file text-info tb-icon"></div>
                    </template>

                    <template slot="operating" slot-scope="props">
                      <div v-if="!props.rowData.users.length" @click="onClickDeleteDataGroup(props.rowData)" class="glyph-icon simple-icon-close text-danger tb-button"></div>
                      <div v-else class="glyph-icon simple-icon-close tb-button-disabled"></div>
                    </template>

                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="dataGroupPagination"
                    @vuetable-pagination:change-page="onDataGroupChangePage"
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
                  <v-tree ref='orgUserTree' :data='orgUserTreeData' :multiple="true" :halfcheck='true' />
                </b-col>
              </b-row>

              <b-row>
                <b-col cols="12" class="text-right">
                  <b-form-group>
                    <b-button @click="onClickSaveDataGroup">{{$t('permission-management.permission-control.save')}}</b-button>
                  </b-form-group>
                </b-col>
              </b-row>

            </b-card>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>

    <div v-show="isLoading" class="loading"></div>

    <b-modal id="modal-delete-role" ref="modal-delete-role" :title="$t('permission-management.permission-control.prompt')">
      {{$t('permission-management.permission-control.delete-role-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="deleteRole" class="mr-1">{{$t('system-setting.ok')}}</b-button>
        <b-button variant="danger" @click="hideModal('modal-delete-role')">{{$t('system-setting.cancel')}}</b-button>
      </template>
    </b-modal>

    <b-modal id="modal-delete-data-group" ref="modal-delete-data-group" :title="$t('permission-management.permission-control.prompt')">
      {{$t('permission-management.permission-control.delete-data-group-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="deleteDataGroup" class="mr-1">{{$t('system-setting.ok')}}</b-button>
        <b-button variant="danger" @click="hideModal('modal-delete-data-group')">{{$t('system-setting.cancel')}}</b-button>
      </template>
    </b-modal>

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

      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/get-all`, {}).then((response) => {
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
          roleName: '',
          note: ''
        },
          roleKeyword: '',
        roleFlag: null,
        roleFlagData: [
            {value: null, text: this.$t('permission-management.permission-control.all')},
            {value: 'admin', text: this.$t('permission-management.permission-control.system-management')},
            {value: 'user', text: this.$t('permission-management.permission-control.business-operating')},
            {value: 'unset', text: this.$t('permission-management.permission-control.no-role')},
        ],
          resourceList: [],
          resourceTreeData: {
            admin: [],
              user: [],
          },
          currentResourceTreeData: [],
          roleCategory: null,
          selectedRole: null,
          isSelectedAllResourcesForRole: false,
          roleVuetableItems: {
            apiUrl: `${apiBaseUrl}/permission-management/permission-control/role/get-by-filter-and-page`,
              perPage: 5,
              fields: [
                  {
                      name: 'roleId',
                      title: this.$t('permission-management.permission-control.serial-number'),
                      // sortField: 'roleId,
                      titleClass: 'text-center',
                      dataClass: 'text-center',
                  },
                  {
                      name: 'roleName',
                      title: this.$t('permission-management.permission-control.role-name'),
                      // sortField: 'dataGroupName',
                      titleClass: 'text-center',
                      dataClass: 'text-center',
                  },
                  {
                      name: '__slot:roleFlag',
                      title: this.$t('permission-management.permission-control.role-flag'),
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
              ]
          },
        dataGroupForm: {
          dataGroupName: '',
          note: '',
        },
        groupKeyword: '',
        groupFlag: null,
        groupFlagData: [
            {value: null, text: this.$t('permission-management.permission-control.all')},
            {value: 'set', text: this.$t('permission-management.permission-control.grouped')},
            {value: 'unset', text: this.$t('permission-management.permission-control.ungrouped')},
        ],
        orgList: [],
        userList: [],
        orgUserTreeData: [],
        selectedDataGroup: null,
        isSelectedAllUsersForDataGroup: false,
        dataGroupVuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/permission-control/data-group/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: 'dataGroupId',
              title: this.$t('permission-management.permission-control.serial-number'),
              // sortField: 'dataGroupId',
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
        resourceList(newVal, oldVal) {
            this.refreshResourceTreeData();
        },
        roleCategory(newVal, oldVal) {
            if(this.selectedRole) {
                this.selectedRole.roleFlag = newVal;
                this.isSelectedAllResourcesForRole = false;
                this.refreshResourceTreeData();
            }
        },
        selectedRole(newVal, oldVal) {
            if(newVal) {
                let roleResourceIds = [];
                newVal.resources.forEach((resource) => {
                    roleResourceIds.push(resource.resourceId);
                });
                this.resourceList.forEach((resource) => {
                    resource.selected = roleResourceIds.includes(resource.resourceId);
                });
                this.roleCategory = newVal.roleFlag;
                this.refreshResourceTreeData();
            }
        },
        isSelectedAllResourcesForRole(newVal, oldVal) {
            if(this.selectedRole) {
                let tempSelectedRole = this.selectedRole;
                tempSelectedRole.resources = newVal ? this.resourceList.filter(resource => resource.resourceCategory === this.selectedRole.roleFlag) : [];
                console.log(tempSelectedRole);
                this.selectedRole = null;
                this.selectedRole = tempSelectedRole;

                this.refreshResourceTreeData();
            }
        },
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
        hideModal(refName) {
            this.$refs[refName].hide();
        },

      onRoleFormSubmit() {
        this.isLoading = true;
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/permission-control/role/create`, {
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
        onRoleFlagChanged(value) {
            this.$refs.roleVuetable.refresh();
        },
        onClickSaveRole() {
            if(this.selectedRole) {
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
                                break;
                            default:

                        }
                    })
                    .catch((error) => {
                        this.isLoading = false;
                    });
            }
        },
        onClickDeleteRole(dataGroup) {
            this.$refs['modal-delete-role'].show();
        },
        deleteRole() {
            if(this.selectedRole) {
                getApiManager()
                    .post(`${apiBaseUrl}/permission-management/permission-control/role/delete`, {
                        'roleId': this.selectedRole.roleId
                    })
                    .then((response) => {
                        this.isLoading = false;
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']:
                                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-deleted`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                this.$refs.roleVuetable.refresh();
                                this.hideModal('modal-delete-role')
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
            if(this.selectedRole) {
                if(this.selectedRole.roleFlag === 'admin') {
                    this.currentResourceTreeData = this.resourceTreeData.admin;
                } else if(this.selectedRole.roleFlag === 'user') {
                    this.currentResourceTreeData = this.resourceTreeData.user;
                } else {
                    this.currentResourceTreeData = [];
                }
            }
        },
        onRoleKeywordChanged(value) {
            this.$refs.roleVuetable.refresh();
        },
        roleVuetableHttpFetch(apiUrl, httpOptions) {
            return getApiManager().post(apiUrl, {
                currentPage: httpOptions.params.page,
                perPage: this.roleVuetableItems.perPage,
                filter: {
                    roleName: this.roleKeyword,
                    category: this.roleFlag
                }
            });
        },
        onRolePaginationData(paginationData) {
            this.$refs.rolePagination.setPaginationData(paginationData)
        },
        onRoleRowClass(dataItem, index) {
            let selectedItem = this.selectedRole;
            if(selectedItem && selectedItem.roleId === dataItem.roleId) {
                return 'selected-row';
            } else {
                return '';
            }
        },
        onRoleRowClicked(dataItem, event) {
            this.selectedRole = JSON.parse(JSON.stringify(dataItem));
        },
        onRoleChangePage(page) {
            this.$refs.roleVuetable.changePage(page);
        },


      onDataGroupFormSubmit() {
        this.isLoading = true;
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/permission-control/data-group/create`, {
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
                this.$refs.dataGroupVuetable.refresh();
                break;
              default:

            }
          })
          .catch((error) => {
            this.isLoading = false;
          });
      },
      onClickSaveDataGroup() {
          if(this.selectedDataGroup) {
              this.isLoading = true;
              let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
              let dataGroupUserIds = [];
              checkedNodes.forEach((node) => {
                  if(node.isUser)dataGroupUserIds.push(node.userId);
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
                          default:

                      }
                  })
                  .catch((error) => {
                      this.isLoading = false;
                  });
          }
      },
        onClickDeleteDataGroup(dataGroup) {
          this.$refs['modal-delete-data-group'].show();
        },
        deleteDataGroup() {
          if(this.selectedDataGroup) {
              getApiManager()
                  .post(`${apiBaseUrl}/permission-management/permission-control/data-group/delete`, {
                      'dataGroupId': this.selectedDataGroup.dataGroupId
                  })
                  .then((response) => {
                      this.isLoading = false;
                      let message = response.data.message;
                      let data = response.data.data;
                      switch (message) {
                          case responseMessages['ok']:
                              this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.data-group-deleted`), {
                                  duration: 3000,
                                  permanent: false
                              });
                              this.$refs.dataGroupVuetable.refresh();
                              this.hideModal('modal-delete-data-group')
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
      onGroupFlagChanged(value) {
          this.$refs.dataGroupVuetable.refresh();
      },
      onGroupKeywordChanged(value) {
          this.$refs.dataGroupVuetable.refresh();
      },
      dataGroupVuetableHttpFetch(apiUrl, httpOptions) {
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.dataGroupVuetableItems.perPage,
          filter: {
              dataGroupName: this.groupKeyword,
              flag: this.groupFlag
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
      onDataGroupChangePage(page) {
        this.$refs.dataGroupVuetable.changePage(page)
      },
    }
  }
</script>
