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
                          <b-form-input v-model="filter.userName"></b-form-input>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.status')">
                          <b-form-select v-model="filter.status" :options="statusSelectData" plain/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.affiliated-institution')">
                          <b-form-select v-model="filter.orgId"
                                         :options="orgNameSelectData"
                                         plain/>
                        </b-form-group>
                      </b-col>

                      <b-col>
                        <b-form-group :label="$t('permission-management.user-category')">
                          <b-form-select v-model="filter.category" :options="categorySelectData" plain/>
                        </b-form-group>
                      </b-col>
                      <b-col></b-col>
                    </b-row>

                  </div>
                  <div class="align-self-center">
                    <b-button size="sm" class="ml-2" variant="info" @click="onSearchButton()">{{
                      $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info" @click="onResetButton()">{{
                      $t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="showCreatePage()" variant="success">{{
                      $t('permission-management.new') }}
                    </b-button>
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
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :data-manager="dataManager"
                    :http-fetch="vuetableHttpFetch"
                    :per-page="vuetableItems.perPage"
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
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                  <b-modal ref="modal-delete" :title="$t('permission-management.prompt')">
                    {{$t('permission-management.organization-delete-prompt')}}
                    <template slot="modal-footer">
                      <b-button variant="primary" @click="deleteOrg()" class="mr-1">
                        {{$t('permission-management.modal-ok')}}
                      </b-button>
                      <b-button variant="danger" @click="hideModal('modal-delete')">
                        {{$t('permission-management.modal-cancel')}}
                      </b-button>
                    </template>
                  </b-modal>

                  <b-modal ref="modal-deactivate" :title="$t('permission-management.prompt')">
                    {{$t('permission-management.organization-deactivate-prompt')}}
                    <template slot="modal-footer">
                      <b-button variant="primary" @click="deactivateOrg()" class="mr-1">
                        {{$t('permission-management.modal-ok')}}
                      </b-button>
                      <b-button variant="danger" @click="hideModal('modal-deactivate')">
                        {{$t('permission-management.modal-cancel')}}
                      </b-button>
                    </template>
                  </b-modal>
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
                    <div class="position-relative img-wrapper p-1" style="min-height: 180px">
                      <img :src="image" class="card-img-top"/>
                      <b-badge variant="primary" pill class="position-absolute badge-bottom-left">NEW</b-badge>
                    </div>
                    <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
                  </b-card>
                  <b-button @click="$refs.profileFile.click()" class="mb-1" variant="light default" size="sm">{{
                    $t('permission-management.upload-image')}}
                  </b-button>
                </b-col>
                <b-col cols="10">
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userName"
                                      :state="!$v.profileForm.userName.$invalid"
                                      :placeholder="$t('permission-management.please-enter-user-name')"></b-form-input>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userName.required) ?
                          $t('permission-management.user.username-field-is-mandatory') :
                          (!$v.profileForm.userName.alphaNum)
                          ?$t('permission-management.user.username-should-be-numerical-or-characters'):(!$v.profileForm.userName.maxLength)?$t('permission-management.user.account-should-less-50-letter'):"&nbsp;"
                          }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userNumber"
                                      :state="!$v.profileForm.userNumber.$invalid"
                                      :placeholder="$t('permission-management.please-enter-user-id')"></b-form-input>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userNumber.required) ?
                          $t('permission-management.user.userNumber-field-is-mandatory') :
                          (!$v.profileForm.userNumber.alphaNum)
                          ?$t('permission-management.user.userNumber-should-be-numerical-or-characters'):
                          (!$v.profileForm.userNumber.maxLength)?$t('permission-management.user.account-should-less-50-letter'):"&nbsp;"
                          }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select v-model="profileForm.gender" :options="genderOptions" plain
                                       :state="!$v.profileForm.gender.$invalid"/>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.gender.required) ?
                          $t('permission-management.user.gender-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.license-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.identityCard"
                                      :state="!$v.profileForm.identityCard.$invalid"
                                      :placeholder="$t('permission-management.please-enter-license-number')"></b-form-input>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.identityCard.required) ?
                          $t('permission-management.user.license-number-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select v-model="profileForm.orgId" :options="orgNameSelectData" plain
                                       :state="!$v.profileForm.orgId.$invalid"/>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.orgId.required) ?
                          $t('permission-management.user.orgId-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.post')}}</template>
                        <b-form-input type="text" v-model="profileForm.post"
                                      :placeholder="$t('permission-management.please-enter-post')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.education')}}</template>
                        <b-form-select v-model="profileForm.education" :options="educationOptions" plain/>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.degree')}}</template>
                        <b-form-select v-model="profileForm.degree" :options="degreeOptions" plain/>
                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row class="mb-3">
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.email')}}</template>
                        <b-form-input type="email" v-model="profileForm.email" :state="!$v.profileForm.email.$invalid"
                                      :placeholder="$t('permission-management.please-enter-email')"></b-form-input>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.email.email) ?
                          $t('permission-management.user.email-field-should-email-format') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.phone')}}</template>
                        <b-form-input type="text" v-model="profileForm.mobile"
                                      :placeholder="$t('permission-management.please-enter-phone')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="6">
                      <b-form-group>
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
                          <b-form-radio v-model="profileForm.category" value="admin">
                            {{$t('permission-management.admin')}}
                          </b-form-radio>
                          <b-form-radio v-model="profileForm.category" value="normal">
                            {{$t('permission-management.normal')}}
                          </b-form-radio>
                        </b-form-radio-group>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.category.required) ?
                          $t('permission-management.user.category-field-is-mandatory') : "&nbsp;" }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userAccount"
                                      :state="!$v.profileForm.userAccount.$invalid"
                                      :placeholder="$t('permission-management.please-enter-user-account')"></b-form-input>
                        <div class="invalid-feedback d-block">
                          {{ (submitted && !$v.profileForm.userAccount.required) ?
                          $t('permission-management.user.account-field-is-mandatory') :
                          (!$v.profileForm.userAccount.alphaNum) ?
                          $t('permission-management.user.account-should-be-numerical-or-characters'):(!$v.profileForm.userAccount.maxLength)?$t('permission-management.user.account-should-less-50-letter'):"&nbsp;"
                          }}
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.password')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <div>
                          <b-form-radio-group inline>
                            <b-form-radio v-model="profileForm.passwordType" value="default">
                              {{$t('permission-management.password-basic')}}
                            </b-form-radio>
                            <b-form-radio v-model="profileForm.passwordType" value="other">
                              {{$t('permission-management.password-other')}}
                            </b-form-radio>
                          </b-form-radio-group>
                        </div>
                        <div>
                          <b-form-input type="password" v-model="profileForm.passwordValue"
                                        :disabled="profileForm.passwordType=='basic'"
                                        :placeholder="$t('permission-management.please-enter-password')"></b-form-input>
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
                      <b-button class="mb-1" @click="saveUserPage()" variant="info default">{{
                        $t('permission-management.save') }}
                      </b-button>
                      <b-button class="mb-1" @click="showTablePage()" variant="danger default">{{
                        $t('permission-management.return') }}
                      </b-button>
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
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {getDirection} from "../../../utils";
  import _ from "lodash";
  import {getApiManager} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {validationMixin} from 'vuelidate';

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  let getOrgById = (orgData, orgId) => {
    for (let i = 0; i < orgData.length; i++) {
      if (orgData[i].orgId == orgId) {
        return orgData[i];
      }
    }
    return 0;
  };

  /**
   * getting orgFull name with parent org
   * @param orgData
   * @returns {*}
   */
  let getOrgFullName = orgData => {
    let orgFullName = '';
    if (orgData == null)
      return orgFullName;
    while (orgData.parent != null) {
      orgFullName += '/' + orgData.orgName;
      orgData = orgData.parent;
    }
    orgFullName = orgData.orgName + orgFullName;
    return orgFullName;
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
          required, alphaNum, maxLength: maxLength(50)
        },
        userNumber: {
          required, alphaNum, maxLength: maxLength(50)
        },
        gender: {
          required,
        },
        identityCard: {
          required
        },
        orgId: {
          required
        },
        email: {
          email
        },
        userAccount: {
          required, alphaNum, maxLength: maxLength(50)
        },
        category: {
          required
        },
      }
    },
    mounted() {
      this.$refs.vuetable.$parent.transform = this.transform.bind(this);
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
        image: '',
        submitted: false,
        tableData: [],
        pageStatus: 'table',
        filter: {
          userName: '',
          status: null,
          orgId: '',
          category: null
        },
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
        orgNameSelectData: {},
        categorySelectData: [
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
        degreeOptions: [
          {value: 'belowcollege', text: this.$t('permission-management.belowcollege')},
          {value: 'bachelor', text: this.$t('permission-management.bachelor')},
          {value: 'master', text: this.$t('permission-management.master')},
          {value: 'doctor', text: this.$t('permission-management.doctor')},
          {value: 'other', text: this.$t('permission-management.other')},
        ],
        profileForm: {
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
        },
        items: [
          {id: 1, first_name: 'Mark', last_name: 'Otto', username: '@mdo'},
          {id: 2, first_name: 'Jacob', last_name: 'Thornton', username: '@fat'},
          {id: 3, first_name: 'Lary', last_name: 'the Bird', username: '@twitter'}
        ],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/get-by-filter-and-page`,
          fields: [
            {
              name: 'userId',
              title: this.$t('permission-management.th-no'),
              sortField: 'userId',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'userNumber',
              title: this.$t('permission-management.th-user-id'),
              sortField: 'userNumber',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'userName',
              title: this.$t('permission-management.th-username'),
              sortField: 'userName',
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
              name: 'orgName',
              title: this.$t('permission-management.th-affiliated-institution'),
              sortField: 'orgName',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'category',
              title: this.$t('permission-management.th-user-category'),
              sortField: 'category',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {

                const dictionary = {
                  "admin": `${this.$t('permission-management.admin')}`,
                  "normal": `${this.$t('permission-management.normal-staff')}`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'userAccount',
              title: this.$t('permission-management.th-account'),
              sortField: 'userAccount',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-action'),
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

        this.orgNameSelectData = selectOptions;

      }
    },
    methods: {
      showCreatePage() { // move to create page
        // reset models
        this.profileForm = {
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
          note: ''
        };
        this.submitted = false;
        // change page to create
        this.pageStatus = 'create';
      },
      showTablePage() {
        this.pageStatus = 'table';
      },
      saveUserPage() {
        this.submitted = true;
        this.$v.$touch();
        if (this.$v.$invalid) {
          return;
        }
        const formData = new FormData();
        for (let key in this.profileForm) {
          if (key !== 'portrait')
            formData.append(key, this.profileForm[key]);
          else
            formData.append(key, this.profileForm[key], this.profileForm[key].name);
        }
        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/user-management/create`, formData)
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
        let files = e.target.files || e.dataTransfer.files;
        if (!files.length)
          return;
        this.createImage(files[0]);
      },
      createImage(file) {
        let image = new Image();
        let reader = new FileReader();
        var vm = this;
        reader.onload = (e) => {
          vm.image = e.target.result;
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
        this.$refs.vuetable.refresh();
      },
      transform(response) {

        let transformed = {};

        let data = response.data;

        transformed.pagination = {
          total: data.total,
          per_page: data.perPage,
          current_page: data.currentPage,
          last_page: data.lastPage,
          from: data.from,
          to: data.to
        };

        transformed.data = [];
        let temp;
        for (let i = 0; i < data.data.length; i++) {
          temp = data.data[i];
          temp.orgName = getOrgFullName(temp.org);
          transformed.data.push(temp)
        }

        return transformed

      },
      vuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

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
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
    }
  }
</script>
