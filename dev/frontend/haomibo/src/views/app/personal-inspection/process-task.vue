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
        <b-col cols="12">
          <div class="mb-4">
            <b-row>
              <b-col cols="8">
                <b-row>

                  <b-col>
                    <b-form-group :label="$t('personal-inspection.process-task.task-number')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('personal-inspection.process-task.operation-mode')">
                      <b-form-select plain/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('personal-inspection.process-task.status')">
                      <b-form-select plain/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('personal-inspection.process-task.on-site')">
                      <b-form-select plain/>
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
                    <b-form-group :label="$t('personal-inspection.process-task.user')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('personal-inspection.process-task.time')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

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
            <b-row>
              <b-col cols="12" class="table-responsive">
                <vuetable
                  ref="taskVuetable"
                  :api-mode="false"
                  :fields="taskVuetableItems.fields"
                  class="table-hover"
                  @vuetable:pagination-data="onTaskVuetablePaginationData"
                >
                </vuetable>
                <vuetable-pagination-bootstrap
                  ref="taskVuetablePagination"
                  @vuetable-pagination:change-page="onTaskVuetableChangePage"
                  :initial-per-page="taskVuetableItems.perPage"
                  @onUpdatePerPage="taskVuetableItems.perPage = Number($event)"
                ></vuetable-pagination-bootstrap>
                <!--                  <b-modal ref="modal-prompt-group" :title="$t('permission-management.prompt')">-->
                <!--                    {{$t('permission-management.user.user-group-delete-prompt')}}-->
                <!--                    <template slot="modal-footer">-->
                <!--                      <b-button variant="primary" @click="fnDeleteUserGroupItem()" class="mr-1">-->
                <!--                        {{$t('permission-management.modal-ok')}}-->
                <!--                      </b-button>-->
                <!--                      <b-button variant="danger" @click="fnHideModal('modal-prompt-group')">-->
                <!--                        {{$t('permission-management.modal-cancel')}}-->
                <!--                      </b-button>-->
                <!--                    </template>-->
                <!--                  </b-modal>-->
              </b-col>
            </b-row>
          </div>
        </b-col>
      </b-row>
    </b-card>



  </div>
</template>

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
  .selected-row {
    background-color: #0000ff20 !important;
  }

  .rounded-span{
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }


</style>

<script>

    import {apiBaseUrl} from "../../../constants/config";
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
    import {getDirection} from "../../../utils";
    import _ from "lodash";
    import {getApiManager} from '../../../api';
    import {responseMessages} from '../../../constants/response-messages';
    import {validationMixin} from 'vuelidate';
    import VTree from 'vue-tree-halower';
    import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree

    const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

    export default {
        components: {
            'vuetable': Vuetable,
            'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
        },
        mounted() {

        },
        data() {
            return {
                isExpanded:false,
                pageStatus: 'table',
                filter: {
                    // TODO: search filter
                },
                // TODO: select options
                taskVuetableItems: {
                    apiUrl: `${apiBaseUrl}/...`,
                    fields: [
                        {
                            name: 'id',
                            title: this.$t('personal-inspection.process-task.serial-number'),
                            sortField: 'id',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'taskNumber',
                            title: this.$t('personal-inspection.process-task.task-number'),
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operationMode',
                            title: this.$t('personal-inspection.process-task.operation-mode'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'status',
                            title: this.$t('personal-inspection.process-task.status'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'onSite',
                            title: this.$t('personal-inspection.process-task.on-site'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'securityInstrument',
                            title: this.$t('personal-inspection.process-task.security-instrument'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'guide',
                            title: this.$t('personal-inspection.process-task.guide'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scanStartTime',
                            title: this.$t('personal-inspection.process-task.scan-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'scanEndTime',
                            title: this.$t('personal-inspection.process-task.scan-end-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judgementStation',
                            title: this.$t('personal-inspection.process-task.judgement-station'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judge',
                            title: this.$t('personal-inspection.process-task.judge'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judgementStartTime',
                            title: this.$t('personal-inspection.process-task.judgement-start-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'judgementEndTime',
                            title: this.$t('personal-inspection.process-task.judgement-end-time'),
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                    ],
                    perPage: 5,
                },

            }
        },
        watch: {
            'vuetableItems.perPage': function (newVal) {
                this.$refs.vuetable.refresh();
            },
            'operatingLogTableItems.perPage': function (newVal) {
                this.$refs.operatingLogTable.refresh();
            },
            orgData(newVal, oldVal) { // maybe called when the org data is loaded from server


                let nest = (items, id = 0) =>
                    items
                        .filter(item => item.parentOrgId == id)
                        .map(item => ({
                            ...item,
                            children: nest(items, item.orgId),
                            id: id++,
                            label: `${item.orgNumber} ${item.orgName}`
                        }));

                this.treeData = nest(newVal)[0];
                let getLevel = (org) => {

                    let getParent = (org) => {
                        for (let i = 0; i < newVal.length; i++) {
                            if (newVal[i].orgId == org.parentOrgId) {
                                return newVal[i];
                            }
                        }
                        return null;
                    };

                    let stepValue = org;
                    let level = 0;
                    while (getParent(stepValue) !== null) {
                        stepValue = getParent(stepValue);
                        level++;
                    }

                    return level;

                };

                let generateSpace = (count) => {
                    let string = '';
                    while (count--) {
                        string += '&nbsp;&nbsp;&nbsp;&nbsp;';
                    }
                    return string;
                };

                let selectOptions = [];

                newVal.forEach((org) => {
                    selectOptions.push({
                        value: org.orgId,
                        html: `${generateSpace(getLevel(org))}${org.orgName}`
                    });
                });

                this.orgNameSelectData = selectOptions;

                this.filter.orgId = this.treeData.orgId;
                this.defaultOrgId = this.treeData.orgId;
                this.fnRefreshOrgUserTreeData();
            },
            userData(newVal) {
                this.fnRefreshOrgUserTreeData();
            },
            selectedUserGroupItem(newVal) {
                if (newVal) {
                    let userGroupList = [];
                    newVal.users.forEach((user) => {
                        userGroupList.push(user.userId);
                    });
                    this.userData.forEach((user) => {
                        user.selected = userGroupList.includes(user.userId);
                    });
                    this.fnRefreshOrgUserTreeData();
                }
            },
            isSelectedAllUsersForDataGroup(newVal) {

                if (this.selectedUserGroupItem) {
                    let tempSelectedUserGroup = this.selectedUserGroupItem;
                    tempSelectedUserGroup.users = newVal ? this.userData : [];
                    this.selectedUserGroupItem = null;
                    this.selectedUserGroupItem = tempSelectedUserGroup;
                }
            }
        },
        methods: {
            onTableListPage() {
                this.pageStatus = 'table';
            },
            onSaveUserPage() {
                this.submitted = true;
                this.$v.$touch();
                if (this.$v.$invalid) {
                    return;
                }

                const formData = new FormData();
                for (let key in this.profileForm) {
                    if (key !== 'portrait')
                        formData.append(key, this.profileForm[key]);
                    else if (this.profileForm['portrait'] !== null)
                        formData.append(key, this.profileForm[key], this.profileForm[key].name);
                }
                // call api
                let finalLink = this.profileForm.userId > 0 ? 'modify' : 'create';
                getApiManager()
                    .post(`${apiBaseUrl}/permission-management/user-management/user/` + finalLink, formData)
                    .then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.profileForm.userId > 0 ? this.$t(`permission-management.user-created-successfully`) : this.$t(`permission-management.user-modify-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                this.onInitialUserData();
                                // back to table
                                this.pageStatus = 'table';
                                break;
                            case responseMessages['used-user-account']://duplicated user account
                                this.$notify('success', this.$t('permission-management.failed'), this.$t(`permission-management.user-account-already-used`), {
                                    duration: 3000,
                                    permanent: false
                                });
                                break;
                        }
                    })
                    .catch((error) => {
                    });
            },
            onAction(action, data, index) {
                let userId = data.userId;
                switch (action) {
                    case 'modify':
                        this.fnModifyItem(data);
                        break;
                    case 'show':
                        this.fnShowItem(data);
                        break;
                    case 'reset-password':
                    case 'active':
                    case 'unblock':
                        this.fnChangeItemStatus(userId, action);
                        break;
                    case 'inactive':
                    case 'blocked':
                        this.fnShowConfDiaglog(userId, action);
                        break;
                    case 'group-remove':
                        this.fnShowUserGroupConfDiaglog(data);
                        break;
                }
            },
            fnHideModal(modal) {
                // hide modal
                this.$refs[modal].hide();
                this.promptTemp = {
                    userId: 0,
                    action: ''
                }
            },
            fnShowConfDiaglog(userId, action) {
                this.promptTemp.userId = userId;
                this.promptTemp.action = action;
                this.$refs['modal-prompt'].show();
            },
            fnModifyItem(data) {
                this.onInitialUserData();
                for (let key in this.profileForm) {
                    if (Object.keys(data).includes(key)) {
                        if (key !== 'portrait' && key !== 'avatar')
                            this.profileForm[key] = data[key];
                        else if (key === 'portrait')
                            this.profileForm.avatar = apiBaseUrl + data['portrait'];
                    }
                }
                this.profileForm.portrait = null;
                this.profileForm.passwordType = 'default';
                this.pageStatus = 'create';
            },
            fnShowItem(data) {
                this.onInitialUserData();
                for (let key in this.profileForm) {
                    if (Object.keys(data).includes(key))
                        if (key !== 'portrait' && key !== 'avatar')
                            this.profileForm[key] = data[key];
                        else if (key === 'portrait')
                            this.profileForm.avatar = apiBaseUrl + data['portrait'];
                }
                this.profileForm.portrait = null;
                this.profileForm.passwordType = 'default';
                this.pageStatus = 'show';
            },
            fnChangeItemStatus(userId = 0, action = '') {
                if (userId === 0)
                    userId = this.promptTemp.userId;
                if (action === '')
                    action = this.promptTemp.action;
                let status = action;
                if (status === 'unblock' || status === 'reset-password')
                    status = 'inactive';
                getApiManager()
                    .post(`${apiBaseUrl}/permission-management/user-management/user/update-status`, {
                        'userId': userId,
                        'status': status,
                    })
                    .then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user-change-status-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });

                                this.$refs.vuetable.refresh();

                                break;
                        }
                    })
                    .catch((error) => {
                    })
                    .finally(() => {
                        this.$refs['modal-prompt'].hide();
                    });

            },
            onFileChange(e) {
                let files = e.target.files || e.dataTransfer.files;
                if (!files.length)
                    return;
                this.onCreateImage(files[0]);
            },
            onCreateImage(file) {
                this.profileForm.avatar = new Image();
                let reader = new FileReader();
                reader.onload = (e) => {
                    this.profileForm.avatar = e.target.result;
                };
                reader.readAsDataURL(file);
                this.profileForm.portrait = file;
            },
            onSearchButton() {

            },
            onResetButton() {

            },
            onInitialUserData() {
                this.profileForm = {
                    status: 'inactive',
                    userId: 0,
                    avatar: '',
                    userName: '',
                    userNumber: '',
                    gender: '',
                    identityCard: '',
                    orgId: '',
                    post: '',
                    education: '',
                    degree: '',
                    email: '',
                    mobile: '',
                    address: '',
                    category: '',
                    userAccount: '',
                    passwordType: 'default',
                    passwordValue: '',
                    note: '',
                    portrait: null
                }
            },
            transform(response) {

                let transformed = {};

                let data = response.data;

                transformed.pagination = {
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
                    temp.orgName = fnGetOrgFullName(temp.org);
                    transformed.data.push(temp)
                }

                return transformed

            },

            //second tab content
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
            fnTransformUserGroupTable(response) {
                this.selectedUserGroupItem = null;
                let transformed = {};

                let data = response.data;

                transformed.operatingLogPagination = {
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
                    transformed.data.push(temp)
                }

                return transformed

            },
            userTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
                return getApiManager().post(apiUrl, {
                    currentPage: httpOptions.params.page,
                    perPage: this.vuetableItems.perPage,
                    filter: {
                        userName: this.filter.userName,
                        status: this.filter.status,
                        orgId: this.filter.orgId,
                        category: this.filter.category,
                    }
                });
            },
            onUserTablePaginationData(paginationData) {
                this.$refs.pagination.setPaginationData(paginationData)
            },
            onUserTableChangePage(page) {
                this.$refs.vuetable.changePage(page)
            },
            onGroupFormSubmit() {
                getApiManager()
                    .post(`${apiBaseUrl}/permission-management/user-management/user-group/create`, this.groupForm)
                    .then((response) => {
                        let message = response.data.message;
                        let data = response.data.data;
                        switch (message) {
                            case responseMessages['ok']: // okay
                                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.group-created-successfully`), {
                                    duration: 3000,
                                    permanent: false
                                });

                                this.$refs.userGroupTable.refresh();

                                break;

                        }
                    })
                    .catch((error) => {
                    })
                    .finally(() => {
                        //
                        this.groupForm = {
                            groupName: null,
                            groupNumber: null,
                            status:'create'
                        };
                    });
            },
            userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

                return getApiManager().post(apiUrl, {
                    currentPage: httpOptions.params.page,
                    perPage: this.operatingLogTableItems.perPage,
                    filter: {
                        groupName: this.groupFilter.name,
                    }
                });
            },
            onTaskVuetablePaginationData(paginationData) {
                this.$refs.taskVuetablePagination.setPaginationData(paginationData)
            },
            onTaskVuetableChangePage(page) {
                this.$refs.taskVuetable.changePage(page)
            },
            onDataGroupRowClass(dataItem, index) {
                let selectedItem = this.selectedUserGroupItem;
                if (selectedItem && selectedItem.userGroupId === dataItem.userGroupId) {
                    return 'selected-row';
                } else {
                    return '';
                }
            },
            onUserGroupTableRowClick(dataItems) {
                this.selectedUserGroupItem = dataItems;
                this.groupForm.status = 'modify';
                console.log(this.selectedUserGroupItem);
            },
            // user tree group
            fnRefreshOrgUserTreeData() {
                let pseudoRootId = 0;
                let nest = (orgData, userData, rootId = pseudoRootId) => {
                    let childrenOrgList = orgData
                        .filter(org => org.parentOrgId === rootId)
                        .map(org => ({
                            ...org,
                            title: org.orgName,
                            expanded: true,
                            children: nest(orgData, userData, org.orgId)
                        }));
                    let childrenUserList = userData
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
                this.orgUserTreeData = nest(this.orgData, this.userData, pseudoRootId);
            },
            onUserGroupSearchButton() {
                this.$refs.userGroupTable.refresh();
            },
            onUserGroupResetButton() {
                this.groupFilter = {
                    name:null
                };
                this.$refs.userGroupTable.refresh();
            },
            onUserGroupCreateButton(){
                this.selectedUserGroupItem = {
                    users:[]
                };
                this.groupForm = {
                    groupNumber:null,
                    groupName:null,
                    status:'create'
                }
            },
            onClickDeleteUserGroup(){
                this.fnShowUserGroupConfDiaglog(this.selectedUserGroupItem);
            },
            onClickCreateUserGroup() {
                if(this.selectedUserGroupItem) {
                    let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
                    let userGroupUserIds = [];
                    checkedNodes.forEach((node) => {
                        if(node.isUser)userGroupUserIds.push(node.userId);
                    });
                    if(userGroupUserIds.length==0){
                        return ;
                    }
                    getApiManager()
                        .post(`${apiBaseUrl}/permission-management/user-management/user-group/create`, {
                            'groupName':this.groupForm.groupName,
                            'groupNumber':this.groupForm.groupNumber,
                            'userIdList': userGroupUserIds
                        })
                        .then((response) => {
                            let message = response.data.message;
                            let data = response.data.data;
                            switch (message) {
                                case responseMessages['ok']:
                                    this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.user-group-modified-successfully`), {
                                        duration: 3000,
                                        permanent: false
                                    });
                                    this.$refs.userGroupTable.refresh();
                                    break;
                                default:

                            }
                        })
                        .catch((error) => {

                        }).finally(() => {
                        //
                        this.groupForm = {
                            groupName: null,
                            groupNumber: null,
                            status:'create'
                        };
                    });
                }
            },
            onClickModifyUserGroup() {
                if(this.selectedUserGroupItem) {
                    let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
                    let userGroupUserIds = [];
                    checkedNodes.forEach((node) => {
                        if(node.isUser)userGroupUserIds.push(node.userId);
                    });
                    getApiManager()
                        .post(`${apiBaseUrl}/permission-management/user-management/user-group/modify`, {
                            'userGroupId': this.selectedUserGroupItem.userGroupId,
                            'userIdList': userGroupUserIds
                        })
                        .then((response) => {
                            let message = response.data.message;
                            let data = response.data.data;
                            switch (message) {
                                case responseMessages['ok']:
                                    this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.user-group-modified-successfully`), {
                                        duration: 3000,
                                        permanent: false
                                    });
                                    this.$refs.userGroupTable.refresh();
                                    break;
                                default:

                            }
                        })
                        .catch((error) => {

                        });
                }
            }
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
  .selected-row {
    background-color: #0000ff20 !important;
  }

  .rounded-span{
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }


</style>
