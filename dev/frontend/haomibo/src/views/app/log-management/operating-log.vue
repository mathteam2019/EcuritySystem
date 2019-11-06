<style lang="scss">

  .rounded-span{
    width: 20px;
    height: 20px;
    border-radius: 10px;
    cursor: pointer;
    background-color: #007bff;
  }


</style>
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

      <b-tab :title="$t('log-management.operating-log.access-log')">
        <b-row v-if="pageStatus=='table'">
          <b-col cols="12">
            <div class="mb-4">

              <b-row>
                <b-col cols="6">
                  <b-row>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.start-time')">
                        <b-form-input v-model="filter.startingTime"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.end-time')">
                        <b-form-input v-model="filter.endingTime"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.access-ip')">
                        <b-form-input v-model="filter.accessIp"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.access-user')">
                        <b-form-input v-model="filter.accessUser"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-end align-items-center">
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
                <b-col cols="12">
                  <vuetable
                    ref="vueTable"
                    :api-mode="false"
                    :fields="vueTableItems.fields"
                    :data-manager="vueTableDataManager"
                    :per-page="vueTableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onvueTablePaginationData"
                  >
                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="vueTablePagination"
                    @vuetable-pagination:change-page="onvueTableChangePage"
                    :initial-per-page="vueTableItems.perPage"
                  ></vuetable-pagination-bootstrap>

                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('log-management.operating-log.operating-log')">
        <b-row>
          <b-col cols="12">
            <div class="mb-4">
              <b-row>
                <b-col cols="8">
                  <b-row>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.account-number')">
                        <b-form-input v-model="filter.accountNumber"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.client-ip')">
                        <b-form-input v-model="filter.clientIp"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.object')">
                        <b-form-input v-model="filter.object"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.operating-result')">
                        <b-form-select v-model="filter.status" :options="statusSelectData" plain/>
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
                      <b-form-group :label="$t('log-management.operating-log.start-time')">
                        <b-form-input v-model="filter.startTime"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group :label="$t('log-management.operating-log.end-time')">
                        <b-form-input v-model="filter.endTime"></b-form-input>
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
                    ref="operatingLogTable"
                    :api-url="operatingLogTableItems.apiUrl"
                    :fields="operatingLogTableItems.fields"
                    :http-fetch="userGroupTableHttpFetch"
                    pagination-path="operatingLogPagination"
                    class="table-hover"
                    @vuetable:pagination-data="onUserGroupTablePaginationData"
                  >
<!--                    <template slot="userGroupNumber" slot-scope="props">-->
<!--                      <span class="cursor-p text-primary" @click="onUserGroupTableRowClick(props.rowData)">{{ props.rowData.groupNumber }}</span>-->
<!--                    </template>-->
<!--                    <template slot="operating" slot-scope="props">-->
<!--                      <b-button variant="danger default btn-square" class="m-0" @click="onAction('group-remove', props.rowData, props.rowIndex)"><i class="icofont-bin"></i> </b-button>-->
<!--                    </template>-->
                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="operatingLogPagination"
                    @vuetable-pagination:change-page="onUserGroupTableChangePage"
                    :initial-per-page="operatingLogTableItems.perPage"
                    @onUpdatePerPage="operatingLogTableItems.perPage = Number($event)"
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
      </b-tab>

    </b-tabs>


  </div>
</template>
<script>
    import _ from 'lodash';
    import {apiBaseUrl} from "../../../constants/config";
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
    import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
    import {getApiManager} from '../../../api';
    import {responseMessages} from '../../../constants/response-messages';


    export default {
        components: {
            'vuetable': Vuetable,
            'vuetable-pagination': VuetablePagination,
            'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
        },
        data() {
            return {
                isExpanded:false,
                pageStatus: 'table',
                filter: {
                    startingTime: '',
                    endingTime:'',
                    accessIp: '',
                    accessUser:'',
                    accountNumber:'',
                    clientIp:'',
                    object:'',
                    startTime:'',
                    endTime: '',

                },
                groupFilter:{
                  name:null
                },
                statusSelectData: [
                    {value: null, text: this.$t('log-management.operating-log.status-all')},
                    {value: 'active', text: this.$t('log-management.operating-log.status-success')},
                    {value: 'inactive', text: this.$t('log-management.operating-log.status-failure')},
                ],
                vueTableItems: {
                    fields: [
                        {
                            name: 'number',
                            title: this.$t('log-management.operating-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'access-time',
                            title: this.$t('log-management.operating-log.access-time'),
                            sortField: 'access-time',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'action',
                            title: this.$t('log-management.operating-log.action'),
                            sortField: 'action',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'accessIp',
                            title: this.$t('log-management.operating-log.access-ip'),
                            sortField: 'accessIp',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                        {
                            name: 'accessUser',
                            title: this.$t('log-management.operating-log.access-user'),
                            sortField: 'accessUser',
                            titleClass: 'text-center',
                            dataClass: 'text-center'
                        },
                    ],
                    perPage: 5,

                },
                tempData: [
                    {
                        "number": 1,
                        "access-time": "00:00",
                        "action": "success",
                        "accessIp": "170.108.49.5",
                        "accessUser": "2139910831",
                    },
                    {
                        "number": 2,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 3,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 4,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 5,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 6,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                    {
                        "number": 7,
                        "access-time": "07:00",
                        "action": "failure",
                        "accessIp": "106.134.49.5",
                        "accessUser": "5436576754",
                    },
                ],
                //second tab content
                operatingLogTableItems: {
                    apiUrl: `${apiBaseUrl}/permission-management/user-management/user-group/get-by-filter-and-page`,
                    perPage: 5,
                    fields: [
                        {
                            name: 'number',
                            title: this.$t('log-management.operating-log.number'),
                            sortField: 'number',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'userId',
                            title: this.$t('log-management.operating-log.user-id'),
                            sortField: 'userId',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'userNumber',
                            title: this.$t('log-management.operating-log.user-number'),
                            sortField: 'userNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'clientIp',
                            title: this.$t('log-management.operating-log.client-ip'),
                            sortField: 'clientIp',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingObject',
                            title: this.$t('log-management.operating-log.object'),
                            sortField: 'operatingObject',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingNumber',
                            title: this.$t('log-management.operating-log.operating-number'),
                            sortField: 'operatingNumber',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operating',
                            title: this.$t('log-management.operating-log.operating'),
                            sortField: 'operating',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingContent',
                            title: this.$t('log-management.operating-log.operating-content'),
                            sortField: 'operatingContent',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingResult',
                            title: this.$t('log-management.operating-log.operating-result'),
                            sortField: 'operatingResult',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'failureCode',
                            title: this.$t('log-management.operating-log.operating-failure-code'),
                            sortField: 'failureCode',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        },
                        {
                            name: 'operatingTime',
                            title: this.$t('log-management.operating-log.operating-time'),
                            sortField: 'operatingTime',
                            titleClass: 'text-center',
                            dataClass: 'text-center',
                        }
                    ],
                },

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
                this.$refs.vuetable.refresh();
            },
            onResetButton() {
                this.filter = {
                    userName: '',
                    status: null,
                    orgId: '',
                    category: null
                };
                if (this.defaultOrgId !== '')
                    this.filter.orgId = this.defaultOrgId;
                this.$refs.vuetable.refresh();
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
            onUserGroupTablePaginationData(paginationData) {
                this.$refs.operatingLogPagination.setPaginationData(paginationData)
            },
            onUserGroupTableChangePage(page) {
                this.$refs.userGroupTable.changePage(page)
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


            vueTableDataManager(sortOrder, pagination) {
                let local = this.tempData;

                // sortOrder can be empty, so we have to check for that as well
                if (sortOrder.length > 0) {
                    local = _.orderBy(
                        local,
                        sortOrder[0].sortField,
                        sortOrder[0].direction
                    );
                }
                pagination = this.$refs.vueTable.makePagination(
                    local.length,
                    this.vueTableItems.perPage
                );

                let from = pagination.from - 1;
                let to = from + this.vueTableItems.perPage;
                return {
                    pagination: pagination,
                    data: _.slice(local, from, to)
                };
            },
            onvueTablePaginationData(paginationData) {
                this.$refs.vueTablePagination.setPaginationData(paginationData);
            },
            onvueTableChangePage(page) {
                this.$refs.vueTable.changePage(page);
            },
        }
    }
</script>
