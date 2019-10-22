<style>
  .badge-bottom-left {
    left: -7px;
    bottom: 0px;
  }
</style>
<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.user-management')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-tabs nav-class="separator-tabs ml-0 mb-5" content-class="tab-content" :no-fade="true">

      <b-tab :title="$t('permission-management.member-table')">
        <b-row v-if="pageStatus=='table'">
          <b-col cols="12">
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
                          <b-form-select v-model="selectedStatus" :options="statusSelectData" plain/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.affiliated-institution')">
                          <b-form-select v-model="selectedAffiliatedInstitution" :options="affiliatedInstitutionSelectData"
                                    plain/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.user-category')">
                          <b-form-select v-model="selectedUserCategory" :options="userCategorySelectData" plain/>
                        </b-form-group>
                      </b-col>
                      <b-col></b-col>
                    </b-row>

                  </div>
                  <div class="align-self-center">
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('permission-management.search') }}</b-button>
                    <b-button size="sm" class="ml-2" variant="info">{{ $t('permission-management.reset') }}</b-button>
                    <b-button size="sm" class="ml-2" @click="showCreatePage()" variant="success">{{ $t('permission-management.new') }}</b-button>
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
                    :fields="vuetableItems.fields"
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
        <b-row v-if="pageStatus=='create'">
          <b-col cols="12">
            <b-card class="mb-4">
              <b-row>
                <b-col cols="2" class="text-right">
                  <b-card class="mb-4" no-body>
                    <div class="position-relative img-wrapper p-1">
                      <img src="/assets/img/profile.jpg" class="card-img-top" />
                      <b-badge variant="primary" pill class="position-absolute badge-bottom-left">NEW</b-badge>
                    </div>

                  </b-card>
                  <b-button class="mb-1" variant="light default" size="sm">{{ $t('permission-management.upload-image') }}</b-button>
                </b-col>
                <b-col cols="10">
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userName"
                                      :placeholder="$t('permission-management.please-enter-user-name')"></b-form-input>
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userName.required) ? $t('permission-management.user.username-field-is-mandatory') : (!$v.profileForm.userName.alphaNum) ?$t('permission-management.user.username-should-be-numerical-or-characters'):"&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userId"
                                      :placeholder="$t('permission-management.please-enter-user-id')"></b-form-input>
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userId.required) ? $t('permission-management.user.userId-field-is-mandatory') : (!$v.profileForm.userId.alphaNum) ?$t('permission-management.user.userId-should-be-numerical-or-characters'):"&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                          class="text-danger">*</span></template>
                       <b-form-select v-model="profileForm.gender" :options="genderOptions" plain />
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.gender.required) ? $t('permission-management.user.gender-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.license-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.identityCard"
                                      :placeholder="$t('permission-management.please-enter-license-number')"></b-form-input>
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.identityCard.required) ? $t('permission-management.user.license-number-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select v-model="profileForm.orgId" :options="affiliatedInstitutionSelectData" plain />
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.orgId.required) ? $t('permission-management.user.orgId-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.post')}}</template>
                        <b-form-input type="text" v-model="profileForm.post"
                                      :placeholder="$t('permission-management.please-enter-post')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.education')}}</template>
                        <b-form-select v-model="profileForm.education" :options="educationOptions" plain />
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.degree')}}</template>
                        <b-form-select v-model="profileForm.degree" :options="degreeOptions" plain />
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row class="mb-3">
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.email')}}</template>
                        <b-form-input type="email" v-model="profileForm.email"
                                      :placeholder="$t('permission-management.please-enter-email')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.phone')}}</template>
                        <b-form-input type="text" v-model="profileForm.mobile"
                                      :placeholder="$t('permission-management.please-enter-phone')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="6">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.address')}}</template>
                        <b-form-input type="text" v-model="profileForm.address"
                                      :placeholder="$t('permission-management.please-enter-address')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.th-user-category')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-radio-group inline>
                          <b-form-radio v-model="profileForm.userCategory" value="admin">{{$t('permission-management.admin')}}</b-form-radio>
                          <b-form-radio v-model="profileForm.userCategory" value="normal">{{$t('permission-management.normal')}}</b-form-radio>
                        </b-form-radio-group>
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userCategory.required) ? $t('permission-management.user.userCategory-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group >
                        <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userAccount"
                                      :placeholder="$t('permission-management.please-enter-user-account')"></b-form-input>
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userAccount.required) ? $t('permission-management.user.account-field-is-mandatory') : (!$v.profileForm.userAccount.alphaNum) ?$t('permission-management.user.account-should-be-numerical-or-characters'):"&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.password')}}&nbsp;<span
                          class="text-danger">*</span></template>
                      <div>
                        <b-form-radio-group inline>
                          <b-form-radio v-model="profileForm.passwordType" value="basic">{{$t('permission-management.password-basic')}}</b-form-radio>
                          <b-form-radio v-model="profileForm.passwordType" value="other">{{$t('permission-management.password-other')}}</b-form-radio>
                        </b-form-radio-group>
                      </div>
                      <div>
                        <b-form-input type="password" v-model="profileForm.passwordValue" :disabled="profileForm.passwordType=='basic'"
                                      :placeholder="$t('permission-management.please-enter-password')"></b-form-input>
                        <div  class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.passwordValue.minLength) ? $t('permission-management.user.passwordValue-length-bigger-6-letter') : "&nbsp;" }}
                        </div>
                      </div>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row>
                    <b-col cols="6">
                      <b-form-group :label="$t('permission-management.note')">
                        <b-form-textarea type="text" v-model="profileForm.note"
                                      :placeholder="$t('permission-management.please-enter-note')"/>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row>
                    <b-col cols="12">
                      <b-button class="mb-1" @click="saveUserPage()"  variant="info default">{{ $t('permission-management.save') }}</b-button>
                      <b-button class="mb-1" @click="showTablePage()" variant="danger default">{{ $t('permission-management.return') }}</b-button>
                    </b-col>
                  </b-row>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.user-group')">
        <b-row>
          <b-col cols="12">
            <b-card class="mb-4" :title="'TODO'">
              <h1>Nice</h1>
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
  import {getDirection} from "../../../utils";
  import _ from "lodash";
  import {getApiManager} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import staticUserTableData from '../../../data/user';
  import {validationMixin} from 'vuelidate';
  const {required, email, minLength, sameAs, alphaNum} = require('vuelidate/lib/validators');

  let getOrgById = (orgData, orgId) => {
    for (let i = 0; i < orgData.length; i++) {
      if (orgData[i].orgId == orgId) {
        return orgData[i];
      }
    }
    return 0;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap
    },
    mixins: [validationMixin],
    validations: {
      profileForm: {
        userName: {
          required, alphaNum
        },
        password: {
          required, minLength: minLength(6)
        },
        userId: {
          required, alphaNum
        },
        gender: {
          required,
        },
        identityCard:{
          required
        },
        orgId:{
          required
        },
        email: {
          required,
          email
        },
        userAccount: {
          required,alphaNum
        },
        userCategory : {
          required
        },
        passwordValue: {
          minLength: minLength(6)
        },
      }
    },
    mounted() {

      this.tableData = staticUserTableData;
      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/get-all`).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.orgData = data;
            break;
        }
      })

    },
    data() {
      return {
        submitted:false,
        tableData: [],
        pageStatus:'table',
        selectedStatus: null,
        selectedAffiliatedInstitution: '',
        selectedUserCategory: null,
        orgData: [],
        direction: getDirection().direction,
        genderOptions: [
          {value: 'male', text: this.$t('permission-management.male')},
          {value: 'female', text: this.$t('permission-management.female')},
          {value: 'unknown', text: this.$t('permission-management.unknown')},
        ],
        statusSelectData: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'active', text: this.$t('permission-management.active')},
          {value: 'inactive', text: this.$t('permission-management.inactive')},
          {value: 'pending', text: this.$t('permission-management.pending')},
          {value: 'blocked', text: this.$t('permission-management.blocked')},
        ],
        affiliatedInstitutionSelectData: {},
        userCategorySelectData: [
          {value: null, text: this.$t('permission-management.all')},
          {value: 'admin', text: this.$t('permission-management.admin')},
          {value: 'normal', text: this.$t('permission-management.normal-staff')}
        ],
        educationOptions: [
            {value: 'belowcollege', text: this.$t('permission-management.belowcollege')},
            {value: 'student', text: this.$t('permission-management.student')},
            {value: 'master_student', text: this.$t('permission-management.master_student')},
            {value: 'doctor_student', text: this.$t('permission-management.doctor_student')},
            {value: 'other', text: this.$t('permission-management.other')},
        ],
        degreeOptions:[
            {value: 'belowcollege', text: this.$t('permission-management.belowcollege')},
            {value: 'bachelor', text: this.$t('permission-management.bachelor')},
            {value: 'master', text: this.$t('permission-management.master')},
            {value: 'doctor', text: this.$t('permission-management.doctor')},
            {value: 'other', text: this.$t('permission-management.other')},
        ],
        profileForm: {
          userName:'',
          userId:'',
          gender:'',
          identityCard:'',
          orgId:'',
          post:'',
          education:'',
          degree:'',
          email:'',
          mobile:'',
          address:'',
          userCategory:'',
          userAccount:'',
          passwordType:'default',
          passwordValue:'',
          note:''
        },
        items: [
          {id: 1, first_name: 'Mark', last_name: 'Otto', username: '@mdo'},
          {id: 2, first_name: 'Jacob', last_name: 'Thornton', username: '@fat'},
          {id: 3, first_name: 'Lary', last_name: 'the Bird', username: '@twitter'}
        ],
        vuetableItems: {
          apiUrl: apiBaseUrl + '/cakes/fordatatable',
          fields: [

            {
              name: 'no',
              title: this.$t('permission-management.th-no'),
              sortField: 'no',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'id',
              title: this.$t('permission-management.th-no'),
              sortField: 'id',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'username',
              title: this.$t('permission-management.th-username'),
              sortField: 'username',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-status'),
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
              title: this.$t('permission-management.th-affiliated-institution'),
              sortField: 'affiliatedInstitution',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'category',
              title: this.$t('permission-management.th-user-category'),
              sortField: 'userCategory',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'account',
              title: this.$t('permission-management.th-account'),
              sortField: 'account',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-action'),
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
      },
      orgData(newVal, oldVal) { // maybe called when the org data is loaded from server

        let id = 0;
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

        this.affiliatedInstitutionSelectData = selectOptions;

      }
    },
    methods: {
      showCreatePage() { // move to create page
        // reset models

        this.submitted = false;
        // change page to create
        this.pageStatus = 'create';
      },
      showTablePage(){
        this.pageStatus = 'table';
      },
      saveUserPage(){
        this.submitted = true;
        this.$v.$touch();
        if (this.$v.$invalid) {
          return;
        }
        console.log(this.profile);
        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/user/create`, this.profileForm)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user-created-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                // back to table
                this.pageStatus = 'table';
                break;
            }
          })
          .catch((error) => {
          });
      },
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
      },
      onFileChange(e) {
        const file = e.target.files[0];
        this.url = URL.createObjectURL(file);
      }
    }
  }
</script>
