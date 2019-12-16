<style lang="scss">
  .bg-organization-structure {
    background: url("../../../assets/img/bg-china-map.png") no-repeat center;
    background-size: contain;
    height: calc(100% - 50px);

    & > div {
      background: transparent !important;
    }


  }
</style>

<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb :heading="$t('menu.organization-management')"/>
          <div class="separator mb-5"></div>
        </b-colxx>
      </b-row>
    </div>


    <b-tabs nav-class="ml-2" :no-fade="true" class="h-100">

      <b-tab :title="$t('permission-management.organization-table')">
        <b-row v-if="pageStatus==='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col class="d-flex">
                <div class="flex-grow-1">

                  <b-row>

                    <b-col>
                      <b-form-group :label="$t('permission-management.organization-name')">
                        <b-form-input v-model="filter.orgName"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.active-state')">
                        <b-form-select :options="statusSelectOptions" v-model="filter.status" plain/>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.parent-organization-name')">
                        <b-form-input v-model="filter.parentOrgName"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col></b-col>
                  </b-row>

                </div>
                <div class="align-self-center">
                  <b-button
                    size="sm"
                    class="ml-2"
                    variant="info default"
                    @click="onSearchButton()"><i class="icofont-search-1"></i>&nbsp;
                    {{ $t('permission-management.search') }}
                  </b-button>
                  <b-button
                    size="sm"
                    class="ml-2"
                    variant="info default"
                    @click="onResetButton()"><i class="icofont-ui-reply"></i>&nbsp;
                    {{ $t('permission-management.reset') }}
                  </b-button>
                  <b-button @click="onExportButton()" size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('org_export')">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button @click="onPrintButton()" size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('org_print')"><i class="icofont-printer"></i>&nbsp;
                    {{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="success default" @click="showCreatePage()" :disabled="checkPermItem('org_create')">
                    <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :http-fetch="vuetableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    track-by="orgId"
                    @vuetable:pagination-data="onPaginationData"
                  >
                    <template slot="actions" slot-scope="props">
                      <div>
                        <template v-if="props.rowData.status=='inactive'">
                          <b-button
                            size="sm"
                            variant="info default btn-square"
                            :disabled="checkPermItem('org_modify')"
                            @click="onAction('modify', props.rowData, props.rowIndex)">
                            <i class="icofont-edit"></i>
                          </b-button>
                          <b-button
                            size="sm"
                            variant="success default btn-square"
                            :disabled="checkPermItem('org_update_status')"
                            @click="onAction('activate', props.rowData, props.rowIndex)">
                            <i class="icofont-check-circled"></i>
                          </b-button>
                          <b-button
                            size="sm"
                            variant="danger default btn-square"
                            :disabled="checkPermItem('org_delete')"
                            @click="onAction('delete', props.rowData, props.rowIndex)">
                            <i class="icofont-bin"></i>
                          </b-button>
                        </template>
                        <template v-if="props.rowData.status=='active'">
                          <b-button
                            size="sm"
                            variant="info default btn-square"
                            disabled>
                            <i class="icofont-edit"></i>
                          </b-button>

                          <template v-if="props.rowData.parentOrgId==0">
                            <b-button
                              size="sm"
                              variant="warning default btn-square"
                              disabled>
                              <i class="icofont-ban"></i>
                            </b-button>
                          </template>
                          <template v-else>
                            <b-button
                              size="sm"
                              variant="warning default btn-square"
                              :disabled="checkPermItem('org_update_status')"
                              @click="onAction('deactivate', props.rowData, props.rowIndex)">
                              <i class="icofont-ban"></i>
                            </b-button>
                          </template>
                          <b-button
                            size="sm"
                            variant="danger default btn-square"
                            disabled>
                            <i class="icofont-bin"></i>
                          </b-button>
                        </template>
                      </div>
                    </template>
                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus==='create'" class="h-100 form-section">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="createPage.orgNumber"
                                :state="!$v.createPage.orgNumber.$dirty ? null : !$v.createPage.orgNumber.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-number')"></b-form-input>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-number') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="createPage.orgName"
                                :state="!$v.createPage.orgName.$dirty ? null : !$v.createPage.orgName.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-name')"></b-form-input>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-name') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled v-model="createPageSelectedParentOrganizationNumber"
                                :placeholder="$t('permission-management.please-select-parent-organization')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :options="parentOrganizationNameSelectOptions"
                                 :state="!$v.createPage.parentOrgId.$dirty ? null : !$v.createPage.parentOrgId.$invalid"
                                 v-model="createPage.parentOrgId" plain/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-select-parent-organization') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                  <b-form-input type="text"
                                v-model="createPage.leader"
                                :placeholder="$t('permission-management.please-enter-organization-leader')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                  <b-form-input type="text"
                                v-model="createPage.mobile"
                                :placeholder="$t('permission-management.please-enter-organization-mobile')"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-note')}}</template>

                  <b-form-textarea
                    v-model="createPage.note"
                    :placeholder="$t('permission-management.please-enter-organization-note')"
                  />

                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button size="sm" variant="success default mr-1" @click="onCreatePageSaveButton()"><i
              class="icofont-save"></i> {{
              $t('permission-management.save-button') }}
            </b-button>
            <b-button size="sm" variant="primary default " @click="onCreatePageBackButton()"><i
              class="icofont-long-arrow-left"></i> {{
              $t('permission-management.back-button') }}
            </b-button>
          </b-col>
          <div class="position-absolute" style="left: 32%;bottom: 8%">
            <img src="../../../assets/img/no_active_stamp.png">
          </div>

        </b-row>
        <b-row v-if="pageStatus==='modify'" class="h-100 form-section">
          <b-col cols="6">
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="modifyPage.orgNumber"
                                :state="!$v.modifyPage.orgNumber.$dirty ? null : !$v.modifyPage.orgNumber.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-number')"></b-form-input>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-number') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                v-model="modifyPage.orgName"
                                :state="!$v.modifyPage.orgName.$dirty ? null : !$v.modifyPage.orgName.$invalid"
                                :placeholder="$t('permission-management.please-enter-organization-name')"></b-form-input>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-enter-organization-name') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" disabled v-model="modifyPageSelectedParentOrganizationNumber"
                                :placeholder="$t('permission-management.please-select-parent-organization')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :options="parentOrganizationNameSelectOptions"
                                 :state="!$v.modifyPage.parentOrgId.$dirty ? null : !$v.modifyPage.parentOrgId.$invalid"
                                 v-model="modifyPage.parentOrgId" plain/>
                  <b-form-invalid-feedback>
                    {{ $t('permission-management.organization-management.please-select-parent-organization') }}
                  </b-form-invalid-feedback>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                  <b-form-input type="text"
                                v-model="modifyPage.leader"
                                :placeholder="$t('permission-management.please-enter-organization-leader')"></b-form-input>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                  <b-form-input type="text"
                                v-model="modifyPage.mobile"
                                :placeholder="$t('permission-management.please-enter-organization-mobile')"></b-form-input>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.organization-note')}}</template>

                  <b-form-textarea
                    v-model="modifyPage.note"
                    :placeholder="$t('permission-management.please-enter-organization-note')"
                  />

                </b-form-group>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button size="sm" variant="success default mr-1" @click="onModifyPageSaveButton()"><i
              class="icofont-save"></i> {{
              $t('permission-management.save-button') }}
            </b-button>
            <b-button v-if="modifyPage.selectedOrg.status==='inactive'" size="sm" variant="warning default mr-1"
                      @click="onAction('activate')"><i class="icofont-check-circled"></i> {{
              $t('permission-management.active')}}
            </b-button>
            <b-button v-else-if="modifyPage.selectedOrg.status==='active'" size="sm" variant="warning default mr-1"
                      @click="onAction('deactivate')"><i class="icofont-ban"></i> {{
              $t('permission-management.action-make-inactive')}}
            </b-button>
            <b-button size="sm" variant="danger default mr-1" @click="onAction('delete')"><i class="icofont-bin"></i> {{
              $t('permission-management.delete')}}
            </b-button>
            <b-button size="sm" variant="primary default" @click="onModifyPageBackButton()"><i
              class="icofont-long-arrow-left"></i> {{
              $t('permission-management.back-button') }}
            </b-button>
          </b-col>
          <div class="position-absolute" style="left: 28%;bottom: 12%">
            <img v-if="modifyPage.selectedOrg.status==='inactive'" src="../../../assets/img/no_active_stamp.png">
            <img v-else-if="modifyPage.selectedOrg.status==='active'" src="../../../assets/img/active_stamp.png">
          </div>
        </b-row>

      </b-tab>

      <b-tab :title="$t('permission-management.organization-structure')">

        <b-row class="h-100">
          <b-col cols="12">
            <div class="table-responsive h-100">
              <div class="bg-organization-structure text-center">
                <h3 style="font-size: 2rem;color: #1a3035" class="font-weight-bold my-4 mb-5 pb-4">
                  <span>{{$t('login.admin-title')}}</span> <span style="color: #047a98">{{$t('permission-management.organization-structure')}}</span>
                </h3>
                <vue2-org-tree
                  :data="treeData"
                  :horizontal="false"
                  :collapsable="false"
                  :label-class-name="treeLabelClass"
                  :render-content="renderTreeContent"
                />
              </div>
            </div>
          </b-col>
        </b-row>

      </b-tab>

    </b-tabs>

    <b-modal centered ref="modal-delete" :title="$t('permission-management.prompt')">
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

    <b-modal centered ref="modal-deactivate" :title="$t('permission-management.prompt')">
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
  </div>
</template>
<script>

  import {apiBaseUrl} from '../../../constants/config';
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap';
  import {validationMixin} from 'vuelidate';
  import Vue2OrgTree from '../../../components/vue2-org-tree'
  import {getApiManager,downLoadFileFromServer,printFileFromServer} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {checkPermissionItem} from "../../../utils";

  const {required} = require('vuelidate/lib/validators');

  let getOrgById = (orgData, orgId) => {
    for (let i = 0; i < orgData.length; i++) {
      if (orgData[i].orgId == orgId) {
        return orgData[i];
      }
    }
    return 0;
  };

  let fnGetOrgLevel = orgData => {
    let level = 0;
    if (orgData == null)
      return level;
    while (orgData.parent != null) {
      level++;
      orgData = orgData.parent;
    }
    return level;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      Vue2OrgTree
    },
    mixins: [validationMixin],
    validations: {
      createPage: { // create page
        orgName: {
          required
        },
        orgNumber: {
          required
        },
        parentOrgId: {
          required
        }
      },
      modifyPage: { // modify page
        orgName: {
          required
        },
        orgNumber: {
          required
        },
        parentOrgId: {
          required
        }
      }
    },
    mounted() {

      this.$refs.vuetable.$parent.transform = this.transform.bind(this);
      checkPermissionItem('org_create');
      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
        type: 'with_parent'
      }).then((response) => {
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
        filter: {
          orgName: '',
          status: null,
          parentOrgName: ''
        }, // used for filtering table
        selectedOrg: {}, // this is used for holding data while delete and update status modals
        createPage: { // create page
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        },
        modifyPage: { // modify page
          selectedOrg: {},
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        },
        orgData: [], // loaded from server when the page is mounted
        pageStatus: 'table', // table, create, modify -> it will change the page

        statusSelectOptions: [ // on the filtering
          {value: null, text: this.$t('permission-management.all')},
          {value: 'active', text: this.$t('permission-management.active')},
          {value: 'inactive', text: this.$t('permission-management.inactive')}
        ],
        parentOrganizationNameSelectOptions: {}, // this is used for both create and modify pages, parent org select box options
        vuetableItems: { // main table options
          apiUrl: `${apiBaseUrl}/permission-management/organization-management/organization/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width:'60px'
            },
            {
              name: 'orgId',
              title: this.$t('permission-management.th-no'),
              sortField: 'orgId',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '4%'
            },
            {
              name: 'orgNumber',
              title: this.$t('permission-management.th-org-number'),
              sortField: 'orgNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%'
            },
            {
              name: 'orgName',
              title: this.$t('permission-management.th-org-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-org-status'),
              sortField: 'status',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '7%',
              callback: (value) => {

                const dictionary = {
                  'active': `<span class="text-success">${this.$t('permission-management.org-status-active')}</span>`,
                  'inactive': `<span class="text-muted">${this.$t('permission-management.org-status-inactive')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];

              }
            },
            {
              name: 'parent',
              title: this.$t('permission-management.th-org-parent-org-number'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {

                return value ? value.orgNumber : this.$t('permission-management.org-none');

              }
            },
            {
              name: 'parent',
              title: this.$t('permission-management.th-org-parent-org-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%',
              callback: (value) => {

                return value ? value.orgName : this.$t('permission-management.org-none');

              }
            },
            {
              name: 'leader',
              title: this.$t('permission-management.th-org-leader'),
              sortField: 'leader',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '8%'
            },
            {
              name: 'mobile',
              title: this.$t('permission-management.th-org-mobile'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '15%'
            },
            {
              name: 'note',
              title: this.$t('permission-management.th-org-note'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-org-actions'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '16%'
            },

          ],
          perPage: 10,
        },
        treeData: { // holds tree data for org diagram
        }

      }
    },
    computed: {
      createPageSelectedParentOrganizationNumber: { // create page selected parent org number ( disabled input but automatically change)
        get() {
          let org = getOrgById(this.orgData, this.createPage.parentOrgId);
          return org ? org.orgNumber : "";
        }
      },
      modifyPageSelectedParentOrganizationNumber: { // modify page selected parent org number ( disabled input but automatically change)
        get() {
          let org = getOrgById(this.orgData, this.modifyPage.parentOrgId);
          return org ? org.orgNumber : "";
        }
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },
      orgData(newVal, oldVal) { // maybe called when the org data is loaded from server

        let id = 0;
        let nest = (items, id = 0, depth = 1) =>
          items
            .filter(item => item.parentOrgId == id)
            .map(item => ({
              ...item,
              children: nest(items, item.orgId, depth + 1),
              id: id++,
              label: `<div class="org-content-top"><span>${depth}</span>${item.orgNumber}</div><div class="org-content-bottom">${item.orgName}</div>`
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

        this.parentOrganizationNameSelectOptions = selectOptions;
        if(selectOptions.length==0)
          this.parentOrganizationNameSelectOptions.push({
            text: this.$t('system-setting.none'),
            value: 0
          })

      }
    },
    methods: {
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/organization-management/organization/export`;
        downLoadFileFromServer(link,params,'organization');
      },
      onPrintButton(){
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let params = {
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/organization-management/organization/print`;
        printFileFromServer(link,params);
      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          orgName: '',
          status: null,
          parentOrgName: ''
        };
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

        for (let i = 0; i < data.data.length; i++) {
          transformed.data.push(data.data[i])
        }

        return transformed

      },

      vuetableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: {
            orgName: this.filter.orgName,
            status: this.filter.status,
            parentOrgName: this.filter.parentOrgName
          }
        });
      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData)
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page)
      },
      onAction(action, data = null, index) { // called when any action button is called from table

        let modifyItem = () => {

          // rest models
          this.modifyPage = {
            selectedOrg: data,
            orgName: data.orgName,
            orgNumber: data.orgNumber,
            parentOrgId: data.parent.orgId,
            leader: data.leader,
            mobile: data.mobile,
            note: data.note
          };

          // change page to modify
          this.pageStatus = 'modify';

          this.$v.modifyPage.$reset();

        };

        let deleteItem = () => {
          this.selectedOrg = data;
          if (data == null)
            this.selectedOrg = this.modifyPage.selectedOrg;
          this.$refs['modal-delete'].show();
        };

        let activateItem = () => {
          let selectedOrgId = 0;
          if (data == null)
            selectedOrgId = this.modifyPage.selectedOrg.orgId;
          else
            selectedOrgId = data.orgId;
          // call api
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/organization-management/organization/update-status`, {
              'orgId': selectedOrgId,
              'status': 'active',
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']: // okay
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-activated-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                  if (this.modifyPage != null)
                    this.modifyPage.selectedOrg.status = 'active';
                  this.$refs.vuetable.refresh();
                  break;

              }
            })
            .catch((error) => {
            });


        };

        let deactivateItem = () => {
          this.selectedOrg = data;
          if (data == null)
            this.selectedOrg = this.modifyPage.selectedOrg;
          this.$refs['modal-deactivate'].show();
        };

        switch (action) {
          case 'modify':
            modifyItem();
            break;
          case 'delete':
            deleteItem();
            break;
          case 'activate':
            activateItem();
            break;
          case 'deactivate':
            deactivateItem();
            break;
        }
      },
      renderTreeContent: function (h, data) { // diagram page settings

        return h('div', {
            domProps: {
              innerHTML: data.label
            }
          }
        );

        // return data.label;
      },
      treeLabelClass: function (data) {
        let level = fnGetOrgLevel(data);
        const labelClasses = ['bg-level-1', 'bg-level-2', 'bg-level-3','bg-level-4','bg-level-5'];
        return `${labelClasses[level % 5]} text-white`;
      },

      showCreatePage() { // move to create page
        // reset models
        this.createPage = {
          orgName: '',
          orgNumber: '',
          parentOrgId: null,
          leader: '',
          mobile: '',
          note: ''
        };
        // change page to create
        this.pageStatus = 'create';
        this.$v.createPage.$reset();
      },
      onCreatePageSaveButton() { // save button is clicked from create page

        this.$v.createPage.$touch();
        if(this.$v.createPage.$invalid) {
          return;
        }

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/create`, {
            'orgName': this.createPage.orgName,
            'orgNumber': this.createPage.orgNumber,
            'parentOrgId': this.createPage.parentOrgId,
            'leader': this.createPage.leader,
            'mobile': this.createPage.mobile,
            'note': this.createPage.note
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-created-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                // back to table
                this.pageStatus = 'table';

                this.$refs.vuetable.refresh();

                break;
            }
          })
          .catch((error) => {
          });


      },
      onCreatePageBackButton() {
        // move to table
        this.pageStatus = 'table';
      },
      onModifyPageSaveButton() { // save button is clicked from modify page

        this.$v.modifyPage.$touch();
        if(this.$v.modifyPage.$invalid) {
          return;
        }

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/modify`, {
            'orgId': this.modifyPage.selectedOrg.orgId,
            'orgName': this.modifyPage.orgName,
            'orgNumber': this.modifyPage.orgNumber,
            'parentOrgId': this.modifyPage.parentOrgId,
            'leader': this.modifyPage.leader,
            'mobile': this.modifyPage.mobile,
            'note': this.modifyPage.note
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // ok
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-modified-successfully`), {
                  duration: 3000,
                  permanent: false
                });

                this.pageStatus = 'table';

                this.$refs.vuetable.refresh();
                break;
            }
          })
          .catch((error) => {
          });


      },
      onModifyPageBackButton() {
        // go back to main table page
        this.pageStatus = 'table';
      },
      hideModal(modal) {
        // hide modal
        this.$refs[modal].hide();
      },
      deleteOrg() {

        let org = this.selectedOrg;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/delete`, {
            'orgId': org.orgId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {

              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'table';
                this.$refs.vuetable.refresh();

                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-users']: // okay
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-user`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-delete'].hide();
          });


      },
      deactivateOrg() {

        let org = this.selectedOrg;

        // call api
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/organization-management/organization/update-status`, {
            'orgId': org.orgId,
            'status': 'inactive',
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.organization-activated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.modifyPage != null)
                  this.modifyPage.selectedOrg.status = 'inactive';
                this.$refs.vuetable.refresh();

                break;
            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-deactivate'].hide();
          });

      },
    }
  }
</script>
