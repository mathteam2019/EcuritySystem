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

      <b-tab :title="$t('permission-management.assign-permission-management.assign-to-user')">
        <b-row v-if="pageStatus==='table'" class="h-100 ">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-4">
              <b-col cols="6">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.user')">
                      <b-form-input v-model="userFilter.userName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.affiliated-org')">
                      <b-form-select :options="orgNameSelectData" v-model="userFilter.orgId" plain/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.role')">
                      <b-form-input v-model="userFilter.roleName" ></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.data-range')">
                      <b-form-input v-model="userFilter.dataRange"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>

              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onClickUserSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onClickUserResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onAssignUserCreatePage()" variant="success default">
                    <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>

            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="userVuetable"
                    :api-url="userVuetableItems.apiUrl"
                    :fields="userVuetableItems.fields"
                    :per-page="userVuetableItems.perPage"
                    class="table-striped"
                    :http-fetch="userVuetableFetch"
                    data-path="data.data"
                    pagination-path="data"
                    track-by="userId"
                    @vuetable:pagination-data="onUserPaginationData"
                  >
                    <template slot="userName" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onUserNameClicked(props.rowData)">
                        {{props.rowData.userName}}
                      </span>
                    </template>

                    <template slot="actions" slot-scope="props">
                      <div>
                        <b-button
                          size="sm"
                          variant="primary default btn-square"
                          @click="editUserRoles(props.rowData)">
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          size="sm"
                          variant="danger default btn-square"
                          @click="promptDeleteUserRoles(props.rowData.userId)">
                          <i class="icofont-bin"></i>
                        </b-button>
                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="userPagination"
                    @vuetable-pagination:change-page="onUserChangePage"
                    :initial-per-page="userVuetableItems.perPage"
                    @onUpdatePerPage="userVuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>

          </b-col>
        </b-row>
        <b-row v-else-if="pageStatus!=='table'" class="h-100">
          <b-col cols="12" class="form-section">
            <b-row class="h-100">
              <b-col cols="5">
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-select
                        v-model="userForm.orgId"
                        :options="orgNameSelectData" plain
                        :state="!$v.userForm.orgId.$invalid"
                        :disabled="pageStatus !== 'create'"
                      />
                      <b-form-invalid-feedback>
                        {{ $t('permission-management.user.orgId-field-is-mandatory') }}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">{{$t('permission-management.assign-permission-management.user')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <b-form-select
                        v-model="userForm.userId"
                        :options="userSelectData" plain
                        :state="!$v.userForm.userId.$invalid"
                        :disabled="pageStatus !== 'create'"
                      />
                      <b-form-invalid-feedback>
                        {{ $t('permission-management.user.userId-field-is-mandatory') }}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">{{$t('menu.account')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label>{{selectedUser.userAccount}}</label>
                    </b-form-group>
                  </b-col>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <label class="">{{selectedUserGender}}</label>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100 w-100">
                      <template slot="label">{{$t('permission-management.assign-permission-management.group.role')}}&nbsp;<span
                        class="text-danger">*</span></template>

                      <v-select
                        class="v-select-custom-style"
                        v-model="userForm.roles" multiple
                        :options="roleSelectData" :dir="direction"
                        :disabled="pageStatus === 'show'"
                      />

                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="9">
                    <b-form-group class="mw-100 w-100">
                      <template slot="label">{{$t('permission-management.assign-permission-management.group.data-range')}}&nbsp;<span
                        class="text-danger">*</span></template>
                      <div class="d-flex ">
                        <div>
                          <b-form-radio-group v-model="userForm.dataRangeCategory" stacked>
                            <b-form-radio class="pb-2" value="person">{{$t('permission-management.assign-permission-management.user-form.one-user-data')}}</b-form-radio>
                            <b-form-radio class="pb-2" value="org">{{$t('permission-management.assign-permission-management.user-form.affiliated-org-user-data')}}</b-form-radio>
                            <b-form-radio class="pb-2" value="org_desc">{{$t('permission-management.assign-permission-management.user-form.affiliated-org-all-user-data')}}</b-form-radio>
                            <b-form-radio class="pb-2" value="all">{{$t('permission-management.assign-permission-management.user-form.all-user-data')}}</b-form-radio>
                            <b-form-radio class="pb-2" value="specified">{{$t('permission-management.assign-permission-management.user-form.select-data-group')}}</b-form-radio>
                          </b-form-radio-group>
                        </div>
                        <div class="align-self-end flex-grow-1 pl-2">
                          <b-form-select
                            v-model="userForm.selectedDataGroupId"
                            :options="dataGroupSelectData" plain
                            :disabled="userForm.dataRangeCategory !== 'specified'"
                          />
                        </div>
                      </div>
                    </b-form-group>
                  </b-col>
                </b-row>

              </b-col>
              <b-col cols="12 " class="align-self-end text-right">
                <b-button size="sm" variant="info default" @click="onUserActionGroup('save-item')" v-if="pageStatus !== 'show'"><i class="icofont-save"></i> {{$t('permission-management.save')}}</b-button>
                <b-button size="sm" variant="danger default" @click="onUserActionGroup('delete-item')" v-if="pageStatus !== 'create'"><i class="icofont-bin"></i> {{$t('permission-management.delete')}}</b-button>
                <b-button size="sm" variant="info default" @click="onUserActionGroup('show-list')"><i class="icofont-long-arrow-left"></i> {{$t('permission-management.return')}}</b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>

      </b-tab>

      <b-tab :title="$t('permission-management.assign-permission-management.assign-to-group')">
        <b-row v-if="groupPageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-4">
              <b-col cols="6">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.user-group')">
                      <b-form-input v-model="groupFilter.groupName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.groupMember')">
                      <b-form-input v-model="groupFilter.userName"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.role')">
                      <b-form-input v-model="groupFilter.role"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('permission-management.assign-permission-management.group.data-range')">
                      <b-form-input v-model="groupFilter.dataRange"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>
              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAssignUserGroupSearchButton()">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="info default" @click="onAssignUserGroupResetButton()">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onAssignUserGroupCreatePage()" variant="success default">
                    <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>

            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="userGroupTable"
                    :api-url="userGroupTableItems.apiUrl"
                    :fields="userGroupTableItems.fields"
                    :http-fetch="userGroupTableHttpFetch"
                    pagination-path="userGroupTablePagination"
                    class="table-hover"
                    @vuetable:pagination-data="onUserGroupTablePaginationData"
                  >
                    <template slot="userNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onActionGroup('show-item', props.rowData)">{{ props.rowData.userNumber }}</span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <div>

                        <b-button
                          size="sm"
                          variant="primary default btn-square" @click="onActionGroup('edit-item',props.rowData)">
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          size="sm"
                          variant="danger default btn-square">
                          <i class="icofont-bin"></i>
                        </b-button>

                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="userGroupTablePagination"
                    @vuetable-pagination:change-page="onUserGroupTableChangePage"
                    :initial-per-page="userGroupTableItems.perPage"
                  ></vuetable-pagination-bootstrap>
                  <b-modal ref="modal-prompt-group" :title="$t('permission-management.prompt')">
                    {{$t('permission-management.user.user-group-delete-prompt')}}
                    <template slot="modal-footer">
                      <b-button variant="primary" @click="fnDeleteUserGroupItem()" class="mr-1">
                        {{$t('permission-management.modal-ok')}}
                      </b-button>
                      <b-button variant="danger" @click="fnHideModal('modal-prompt-group')">
                        {{$t('permission-management.modal-cancel')}}
                      </b-button>
                    </template>
                  </b-modal>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-else-if="groupPageStatus==='create'" class="h-100">
          <b-col cols="12" class="form-section">
            <b-row class="h-100">
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.user-group')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select v-model="groupForm.userGroup" :options="groupUserGroupOptions" plain/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted &&!$v.groupForm.userGroup.required) ?
                    $t('permission-management.assign-permission-management.group.user-group-mandatory') : "&nbsp;" }}
                  </div>
                </b-form-group>
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.member')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <label class="">{{selectedUserGroupMember}}</label>
                  <div class="invalid-feedback d-block">
                    {{ (submitted &&!$v.groupForm.selectedUserGroupMembers.required) ?
                    $t('permission-management.assign-permission-management.group.group-member-not-exit') : "&nbsp;" }}
                  </div>
                </b-form-group>
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.assign-permission-management.group.role')}}&nbsp;<span
                    class="text-danger">*</span></template>

                  <v-select class="v-select-custom-style" v-model="groupForm.role" multiple :options="roleSelectData"
                            :dir="direction"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted &&!$v.groupForm.role.required) ?
                    $t('permission-management.assign-permission-management.group.role-mandatory') : "&nbsp;" }}
                  </div>

                </b-form-group>
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.data-range')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <div class="d-flex ">
                    <div>
                      <b-form-radio-group stacked v-model="groupForm.dataRange">
                        <b-form-radio value="person" class="pb-2">
                          {{$t('permission-management.assign-permission-management.group.one-user-data')}}
                        </b-form-radio>
                        <b-form-radio value="group" class="pb-2">
                          {{$t('permission-management.assign-permission-management.group.group-user-data')}}
                        </b-form-radio>
                        <b-form-radio value="all" class="pb-2">
                          {{$t('permission-management.assign-permission-management.group.all-user-data')}}
                        </b-form-radio>
                        <b-form-radio value="specified" class="pb-2">
                          {{$t('permission-management.assign-permission-management.group.select-data-group')}}
                        </b-form-radio>
                      </b-form-radio-group>
                    </div>
                    <div class="align-self-end flex-grow-1 pl-2">
                      <b-form-select :disabled="groupForm.dataRange!='specified'" v-model="groupForm.filterGroup"
                                     :options="groupUserGroupOptions" plain/>
                    </div>
                  </div>
                  <div class="invalid-feedback d-block">
                    {{ (submitted &&!$v.groupForm.dataRange.required) ?
                    $t('permission-management.assign-permission-management.group.data-range-mandatory') : "&nbsp;" }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="12" class="align-self-end text-right">
                <b-button variant="info default" size="sm" @click="onActionGroup('save-item')"><i
                  class="icofont-save"></i> {{$t('permission-management.save')}}
                </b-button>
                <b-button variant="danger default" size="sm" @click="onActionGroup('delete-item')"><i
                  class="icofont-bin"></i> {{$t('permission-management.delete')}}
                </b-button>
                <b-button variant="info default" size="sm" @click="onActionGroup('show-list')"><i
                  class="icofont-long-arrow-left"></i> {{$t('permission-management.return')}}
                </b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>

    <b-modal ref="modal-user-role-delete" :title="$t('permission-management.prompt')">
      {{$t('permission-management.organization-delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="deleteUserRole()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-user-role-delete')">
          {{$t('permission-management.modal-cancel')}}
        </b-button>
      </template>
    </b-modal>

  </div>
</template>
<script>

  import _ from 'lodash';
  import {apiBaseUrl} from '../../../constants/config';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap';
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";
  import {validationMixin} from 'vuelidate';
  const { required } = require('vuelidate/lib/validators');

  import Vue2OrgTree from 'vue2-org-tree'
  import {getApiManager} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';

  export default {
    components: {
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      Vue2OrgTree
    },
    mixins: [validationMixin],
    validations: {
      userForm: {
        orgId: {
          required
        },
        userId: {
          required
        }
      },
      groupForm: {
        userGroup: {
          required
        },
        role: {
          required,
        },
        dataRange: {
          required
        },
        selectedUserGroupMembers: {
          required
        }
      },
    },
    mounted() {

      /////////////////////////////////////////////////
      /////////// Load org data from server ///////////
      /////////////////////////////////////////////////

      let rootOrgId = 0;
      let nest = (items, rootId) =>
        items
          .filter(item => item.parentOrgId == rootId)
          .map(item => ({
            ...item,
            children: nest(items, item.orgId),
          }));

      let generateSpace = (count) => {
        let string = '';
        while (count--) {
          string += '&nbsp;&nbsp;&nbsp;&nbsp;';
        }
        return string;
      };

      let indentData = (orgTreeData, level) => {
        let result = [];
        orgTreeData.forEach((org) => {
          result.push({
            value: org.orgId,
            html: `${generateSpace(level)}${org.orgName}`
          });
          result.push(...indentData(org.children, level + 1));
        });
        return result;
      };

      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
        type: 'with_parent'
      }).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgData = data;
            this.orgTreeData = nest(this.orgData, rootOrgId);
            this.orgNameSelectData = indentData(this.orgTreeData, 0);
            break;
        }
      });

      ///////////////////////////////////////////////////////////
      ////////////// Load user list from server /////////////////
      ///////////////////////////////////////////////////////////

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

      ////////////////////////////////////////////////////////////
      //////////// Load role list from server ////////////////////
      ////////////////////////////////////////////////////////////

      getApiManager().post(`${apiBaseUrl}/permission-management/assign-permission-management/role/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.roleData = data;
            this.roleSelectData = this.roleData.map(role => ({
              label: role.roleName,
              value: role.roleId
            }));
            break;
          default:

        }
      });

      ////////////////////////////////////////////////////////////
      //////////// Load data group from the server ///////////////
      ////////////////////////////////////////////////////////////

      getApiManager().post(`${apiBaseUrl}/permission-management/permission-control/data-group/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.dataGroupList = data;
            this.dataGroupSelectData = this.dataGroupList.map(dataGroup => ({
              value: dataGroup.dataGroupId,
              text: dataGroup.dataGroupName
            }));
            break;
          default:

        }
      });

      ///////////////////////////////////////////////////////////
      ////////////// Load user group list from server /////////////////
      ///////////////////////////////////////////////////////////
      getApiManager().post(`${apiBaseUrl}/permission-management/assign-permission-management/user-group/get-all`, {}).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.userGroupData = data;
            data.forEach(group => {
              this.groupUserGroupOptions.push(
                {text: group.groupName, value: group.userGroupId}
              )
            });
            break;
          default:
        }
      });

      this.$refs.userGroupTable.$parent.transform = this.fnTransformUserGroupTable.bind(this);

    },
    data() {
      return {
        direction: getDirection().direction,
        userFilter: {
          userName: '',
          orgId: null,
          roleName: '',
          dataRange: ''
        }, // used for filtering table
        selectedUserId: null, // this is used for holding data while delete and update status modals
          userVuetableItems: { // main table options
              apiUrl: `${apiBaseUrl}/permission-management/assign-permission-management/user/get-by-filter-and-page`,
              fields: [
                {
                  name: '__checkbox',
                  titleClass: 'text-center',
                  dataClass: 'text-center'
                },
                {
                  name: 'userId',
                  title: this.$t('permission-management.th-no'),
                  sortField: 'userId',
                  titleClass: 'text-center',
                  dataClass: 'text-center'
                },
                {
                  name: '__slot:userName',
                  title: this.$t('permission-management.assign-permission-management.user'),
                  titleClass: 'text-center',
                  dataClass: 'text-center'
                },
                {
                  name: 'gender',
                  title: this.$t('permission-management.gender'),
                  titleClass: 'text-center',
                  dataClass: 'text-center',
                  callback: (value) => {
                      const dictionary = {
                          "male": `<span>${this.$t('permission-management.male')}</span>`,
                          "female": `<span>${this.$t('permission-management.female')}</span>`,
                          "unknown": `<span>${this.$t('permission-management.unknown')}</span>`,
                      };
                      if (!dictionary.hasOwnProperty(value)) return '';
                      return dictionary[value];
                  }
                },
                {
                  name: 'userAccount',
                  title: this.$t('permission-management.th-account'),
                  titleClass: 'text-center',
                  dataClass: 'text-center',
                },
                {
                    name: 'org',
                    title: this.$t('permission-management.assign-permission-management.affiliated-org'),
                    titleClass: 'text-center',
                    dataClass: 'text-center',
                    callback: (org) => {
                        return org.orgName;
                    }
                },
                {
                    name: 'roles',
                    title: this.$t('permission-management.assign-permission-management.group.role'),
                    titleClass: 'text-center',
                    dataClass: 'text-center',
                    callback: (roles) => {
                        return roles.map((role) => role.roleName).join(', ');
                    }
                },
                {
                    name: 'dataRangeCategory',
                    title: this.$t('permission-management.assign-permission-management.group.data-range'),
                    sortField: 'leader',
                    titleClass: 'text-center',
                    dataClass: 'text-center',
                    callback: (dataRangeCategory) => {
                        if(dataRangeCategory === 'person') {
                            return this.$t('permission-management.assign-permission-management.user-form.one-user-data');
                        } else if(dataRangeCategory === 'org') {
                            return this.$t('permission-management.assign-permission-management.user-form.affiliated-org-user-data');
                        }else if (dataRangeCategory === 'org_desc') {
                            return this.$t('permission-management.assign-permission-management.user-form.affiliated-org-all-user-data');
                        } else if(dataRangeCategory === 'all') {
                            return this.$t('permission-management.assign-permission-management.user-form.all-user-data');
                        } else if(dataRangeCategory === 'specified') {
                            return this.$t('permission-management.assign-permission-management.user-form.select-data-group');
                        } else {
                            return '';
                        }
                    }
                },
                {
                    name: '__slot:actions',
                    title: this.$t('permission-management.th-org-actions'),
                    titleClass: 'text-center btn-actions',
                    dataClass: 'text-center'
                },
              ],
              perPage: 5,
          },
        userTempData: [
          {
              userId: 1,
              userName: 'user1',
              gender: 'male',
              userAccount: 'u-1',
              affiliatedOrg: '总部/生产部',
              role: 'role-1',
              dataRange: '个人数据',
          }
        ],
        orgData: [], // loaded from server when the page is mounted
        orgTreeData: [],
        userList: [],
        pageStatus: 'table', // table, create, modify -> it will change the page
        roleData: [],
        dataGroupList: [],
        orgNameSelectData: [],
        userSelectData: [],
        roleSelectData: [],
        dataGroupSelectData: [],
        userForm: {
          orgId: null,
          userId: null,
          nextUserId: null, // when edit or show user's role, userId should be stored here.
          roles: [],
          dataRangeCategory: "person",
          selectedDataGroupId: null
        },
        selectedUser: {},
        selectedUserGender: '',

        //TODO assign permission management for user group part
        groupForm: {
          userGroup: null,
          role: null,
          dataRange: "person",
          filterGroup: null,
          selectedUserGroupMembers: [],
        },
        selectedUserGroupItem:null,
        submitted: false,
        userGroupData: [],
        selectedUserGroupMember: "",
        groupUserGroupOptions: [],
        groupPageStatus: 'table', //table, create
        groupFilter: {
          groupName: null,
          userName: null,
          role: null,
          dataRange: null,
          filterGroup: null
        },
        userGroupTableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/assign-permission-management/user-group/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'userGroupId',
              title: this.$t('permission-management.th-no'),
              sortField: 'number',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'groupName',
              title: this.$t('permission-management.assign-permission-management.group.user-group'),
              sortField: 'groupName',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'groupMember',
              title: this.$t('permission-management.assign-permission-management.group.groupMember'),
              sortField: 'groupMember',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'groupRole',
              title: this.$t('permission-management.assign-permission-management.group.role'),
              sortField: 'groupRole',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'dataRangeCategory',
              title: this.$t('permission-management.assign-permission-management.group.data-range'),
              sortField: 'leader',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (dataRangeCategory) => {
                if(dataRangeCategory === 'person') {
                  return this.$t('permission-management.assign-permission-management.user-form.one-user-data');
                } else if(dataRangeCategory === 'group') {
                  return this.$t('permission-management.assign-permission-management.group.group-user-data');
                } else if(dataRangeCategory === 'all') {
                  return this.$t('permission-management.assign-permission-management.user-form.all-user-data');
                } else if(dataRangeCategory === 'specified') {
                  return this.$t('permission-management.assign-permission-management.user-form.select-data-group');
                } else {
                  return '';
                }
              }
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.user.operating'),
              titleClass: 'text-center btn-actions',
              dataClass: 'text-center'
            }
          ],
        }
      }
    },
    watch: {
      'userVuetableItems.perPage': function (newVal) {
          this.$refs.userVuetable.refresh();
      },
      'userGroupTableItems.perPage': function (newVal) {
        this.$refs.userGroupTable.refresh();
      },
      'userForm.orgId': function(newVal) {
        this.userSelectData = this.userList.filter(user => user.orgId === newVal)
          .map(user => ({
            value: user.userId,
            text: user.userName,
          }));
        this.userForm.userId = this.userForm.nextUserId;
        this.userForm.nextUserId = null;
      },
      'userForm.userId': function(newVal) {
        this.selectedUser = {};
        this.userList.forEach(user => {
          if(user.userId === newVal) {
            this.selectedUser = user;
          }
        });

        if (this.selectedUser.gender === 'male') {
          this.selectedUserGender = this.$t('permission-management.male');
        } else if (this.selectedUser.gender === 'female') {
          this.selectedUserGender = this.$t('permission-management.female');
        } else if (this.selectedUser.gender === 'other') {
          this.selectedUserGender = this.$t('permission-management.unknown');
        } else {
            this.selectedUserGender = '';
        }
      },
      'userForm.dataRangeCategory': function (newVal) {
        this.userForm.selectedDataGroupId = null;
      },
      'groupForm.userGroup': function (newVal, oldVal) {
        this.groupForm.selectedUserGroupMembers = null;
        if (this.userGroupData.length === 0)
          this.selectedUserGroupMember = "";
        else {
          let userGroupMembers = [];
          this.userGroupData.forEach(group => {
            if (group.userGroupId === newVal) {
              if (group.users != null && group.users.length > 0) {
                group.users.forEach(user => {
                  userGroupMembers.push(user.userName);
                });
              }
            }
          });
          this.groupForm.selectedUserGroupMembers = userGroupMembers.length > 0 ? 1 : null;
          this.selectedUserGroupMember = userGroupMembers.join(",");
        }

      }
    },
    methods: {

      userVuetableFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userVuetableItems.perPage,
          filter: {
            userName: this.userFilter.userName,
            orgId: this.userFilter.orgId,
            roleName: this.userFilter.roleName
          }
        });
      },
      onUserPaginationData(paginationData) {
          this.$refs.userPagination.setPaginationData(paginationData)
      },
      onUserChangePage(page) {
        this.$refs.userVuetable.changePage(page)
      },
      hideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
      },
      onUserActionGroup(value) {
        switch (value) {
            case 'save-item':
                if(!this.$v.userForm.$invalid && this.userForm.dataRangeCategory) {
                    getApiManager()
                        .post(`${apiBaseUrl}/permission-management/assign-permission-management/user/assign-role-and-data-range`, {
                            userId: this.userForm.userId,
                            roleIdList: this.userForm.roles.map(selectedRole => selectedRole.value),
                            dataRangeCategory: this.userForm.dataRangeCategory,
                            selectedDataGroupId: this.userForm.selectedDataGroupId
                        }).then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']:
                                this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-created`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                this.userForm = {
                                    orgId: null,
                                    userId: null,
                                    roles: [],
                                    dataRangeCategory: null,
                                    selectedDataGroupId: null
                                };
                                this.selectedUser = {};
                                this.selectedUserGender = '';
                                break;
                            default:
                        }
                    });
                }
                break;
          case 'show-list':
            this.pageStatus = 'table';
            break;
          case 'delete-item':
            this.promptDeleteUserRoles(this.userForm.userId);
            break;
        }
      },

      onUserNameClicked(userWithRole) {
        this.userForm.orgId = userWithRole.org.orgId;
        this.userForm.nextUserId = userWithRole.userId;
        this.userForm.roles = userWithRole.roles.map(role => ({
          label: role.roleName,
          value: role.roleId
        }));
        this.userForm.dataRangeCategory = userWithRole.dataRangeCategory;
        // TODO: determine this.userForm.selectedDataGroupId
        this.pageStatus = 'show';
      },

      promptDeleteUserRoles(userId) {
        this.selectedUserId = userId;
        this.$refs['modal-user-role-delete'].show();
      },

      deleteUserRole() {
        this.hideModal('modal-user-role-delete');

        if(this.selectedUserId) {
            getApiManager()
                .post(`${apiBaseUrl}/permission-management/assign-permission-management/user/assign-role-and-data-range`, {
                    userId: this.selectedUserId,
                    roleIdList: [],
                    dataRangeCategory: '',
                    selectedDataGroupId: null
                }).then((response) => {
                    let message = response.data.message;
                    let data = response.data.data;
                    switch (message) {
                        case responseMessages['ok']:
                            this.$notify('success', this.$t('permission-management.permission-control.success'), this.$t(`permission-management.permission-control.role-deleted`), {
                                duration: 3000,
                                permanent: false
                            });
                            this.selectedUserId = null;
                            this.pageStatus = 'table';
                            this.$refs.userVuetable.refresh();
                            break;
                        default:
                    }
            });

        }
      },

      editUserRoles(userWithRole) {
        this.userForm.orgId = userWithRole.org.orgId;
        this.userForm.nextUserId = userWithRole.userId;
        this.userForm.roles = userWithRole.roles.map(role => ({
            label: role.roleName,
            value: role.roleId
        }));
        this.userForm.dataRangeCategory = userWithRole.dataRangeCategory;
        // TODO: determine this.userForm.selectedDataGroupId
        this.pageStatus = 'modify';
      },

      //TODO assign user group point
      onActionGroup(value,data = null) {
        this.selectedUserGroupItem = data;
        switch (value) {
          case 'show-list':
            this.groupPageStatus = 'table';
            break;
          case 'save-item':
            this.fnAssignUserGroupItem();
            break;
          case 'show-item':
            this.groupPageStatus = 'show';
            break;
          case 'edit-item':
            this.groupPageStatus = 'edit';
            break;
          case 'delete-item':
            break;
        }
      },
      onAssignUserGroupSearchButton() {
        this.$refs.userGroupTable.refresh();
      },
      onAssignUserGroupResetButton() {
        this.groupFilter =  {
          groupName: null,
            userName: null,
            role: null,
            dataRange: null,
            filterGroup: null
        };
        this.$refs.userGroupTable.refresh();
      },

        onAssignUserCreatePage(){
            this.pageStatus = 'create';
        },

      onAssignUserGroupCreatePage(){
        this.groupPageStatus = 'create';
      },
      userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userGroupTableItems.perPage,
          filter: {
            groupName: this.groupFilter.groupName,
            userName: this.groupFilter.userName,
            roleName: this.groupFilter.role
          }
        });
      },
      onUserGroupTablePaginationData(paginationData) {
        this.$refs.userGroupTablePagination.setPaginationData(paginationData)
      },

      onUserGroupTableChangePage(page) {
        this.$refs.userGroupTable.changePage(page)
      },
      fnShowUserGroupConfDiaglog(userGroupItem) {
        this.selectedUserGroupItem = userGroupItem;
        this.$refs['modal-prompt-group'].show();
      },
      fnDeleteUserGroupItem() {
        if (this.selectedUserGroupItem && this.selectedUserGroupItem.userGroupId > 0) {
          this.$refs['modal-prompt-group'].hide();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/user-management/user-group/delete`, {
              userGroupId: this.selectedUserGroupItem.userGroupId
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']: // okay
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.group-removed-successfully`), {
                    duration: 3000,
                    permanent: false
                  });

                  this.$refs.userGroupTable.refresh();
                  this.selectedUserGroupItem = null;
                  break;
                case responseMessages['has-children']: // okay
                  this.$notify('success', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;

              }
            })
            .catch((error) => {
            })
            .finally(() => {

            });
        }
      },

      fnAssignUserGroupItem() {
        this.submitted = true;
        this.$v.groupForm.$touch();
        if (this.$v.groupForm.$invalid) {
          return;
        }
        let dataRangeGroupID = 0;
        if (this.groupForm.dataRange === 'specified')
          dataRangeGroupID = this.groupForm.filterGroup;
        let groupSelectedRoles = [];
        this.groupForm.role.forEach(role => {
          groupSelectedRoles.push(role.value);
        });
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/assign-permission-management/user-group/assign-role-and-data-range`, {
            userGroupId: this.groupForm.userGroup,
            dataRangeCategory: this.groupForm.dataRange,
            selectedDataGroupId: dataRangeGroupID,
            roleIdList: groupSelectedRoles
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.group-removed-successfully`), {
                  duration: 3000,
                  permanent: false
                });

                this.$refs.userGroupTable.refresh();
                this.selectedUserGroupItem = null;
                break;
              case responseMessages['has-children']: // okay
                this.$notify('success', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-child`), {
                  duration: 3000,
                  permanent: false
                });
                break;

            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.groupPageStatus = 'table';

          });
      },

      fnTransformUserGroupTable(response) {
        let transformed = {};

        let data = response.data;

        transformed.userGroupTablePagination = {
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
          let userMembers = [];
          temp.users.forEach(user => {
            userMembers.push(user.userName);
          });
          let groupRoles = [];
          temp.roles.forEach(role => {
            groupRoles.push(role.roleName);
          });
          temp.groupRole = groupRoles.join(',');
          temp.groupMember = userMembers.join(',');
          transformed.data.push(temp);
        }

        return transformed

      },
      onUserTablePaginationData(paginationData) {
        this.$refs.userGroupTablePagination.setPaginationData(paginationData)
      },
      onClickUserSearchButton() {
          this.$refs.userVuetable.refresh();
      },
      onClickUserResetButton() {
          this.userFilter = {
              userName: '',
              orgId: null,
              roleName: '',
              dataRange: ''
          };
          this.$refs.userVuetable.refresh();
      },

    }
  }
</script>
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

  span.cursor-p {
    cursor: pointer !important;
  }

</style>
