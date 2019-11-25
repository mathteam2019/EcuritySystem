<template>
  <div>
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('system-setting.site-list')">
        <b-row v-if="pageStatus=='table'" class="h-100">
          <b-col cols="12 d-flex flex-column">
            <b-row class="pt-2">
              <b-col cols="6">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('system-setting.site')">
                      <b-form-input v-model="filterOption.fieldDesignation"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('system-setting.status-active')">
                      <b-form-select v-model="filterOption.status" :options="stateOptions" plain/>
                    </b-form-group>
                  </b-col>

                  <b-col>
                    <b-form-group :label="$t('system-setting.super-site')">
                      <b-form-input v-model="filterOption.parentFieldDesignation"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-col>

              <b-col cols="6" class="d-flex justify-content-end align-items-center">
                <div>
                  <b-button size="sm" class="ml-2" @click="onSearchButton()" variant="info default">
                    <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onResetButton()" variant="info default">
                    <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" variant="outline-info default">
                    <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                  </b-button>
                  <b-button size="sm" class="ml-2" @click="onAction('create')" variant="success default">
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
                    :http-fetch="siteTableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    pagination-path="pagination"
                    @vuetable:pagination-data="onPaginationData"
                    class="table-striped"
                  >
                    <template slot="fieldSerial" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show',props.rowData)">{{ props.rowData.fieldSerial }}</span>
                    </template>

                    <div slot="operating" slot-scope="props">

                      <b-button
                        size="sm" @click="onAction('edit',props.rowData)"
                        variant="primary default btn-square"
                        :disabled="props.rowData.status === 'active'">
                        <i class="icofont-edit"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === 'inactive'"
                        size="sm" @click="onAction('activate',props.rowData)"
                        variant="success default btn-square">
                        <i class="icofont-check-circled"></i>
                      </b-button>

                      <b-button
                        v-if="props.rowData.status === 'active'"
                        size="sm" @click="onAction('inactivate',props.rowData)"
                        variant="warning default btn-square"
                        :disabled="props.rowData.parentFieldId === 0"
                      >
                        <i class="icofont-ban"></i>
                      </b-button>

                      <b-button
                        :disabled="props.rowData.status==='active'"
                        size="sm" @click="onAction('delete',props.rowData)"
                        variant="danger default btn-square"
                      >
                        <i class="icofont-bin"></i>
                      </b-button>

                    </div>
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
        <b-row v-if="pageStatus !== 'table' &&pageStatus !== 'show' " class="h-100">
          <b-col cols="12 d-flex flex-column form-section " class="position-relative">
            <b-row>
              <b-col cols="6">
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldSerial"
                                    :state="!$v.siteForm.fieldSerial.$invalid"
                                    :placeholder="$t('system-setting.please-enter-site-no')"></b-form-input>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldDesignation"
                                    :state="!$v.siteForm.fieldDesignation.$invalid"
                                    :placeholder="$t('system-setting.please-enter-site-name')"></b-form-input>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="selectedParentSerial" disabled></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-select :disabled="pageStatus==='edit'" :options="superSiteOptions"
                                     :state="!$v.siteForm.parentFieldId.$invalid"
                                     v-model="siteForm.parentFieldId" plain/>
                      <b-form-invalid-feedback>{{$t('permission-management.permission-control.required-field')}}
                      </b-form-invalid-feedback>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.manager')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.leader"
                                    :placeholder="$t('system-setting.please-enter-manager')"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.system-phone')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.mobile" :placeholder="''"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group :label="$t('system-setting.remarks')">
                      <b-form-textarea rows="4" v-model="siteForm.note"
                                       :placeholder="$t('system-setting.please-enter-remarks')"></b-form-textarea>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 align-items-end">
              <b-col cols="12" class="d-flex justify-content-end">
                <div class="mr-3">
                  <b-button @click="onAction('save')" size="sm" variant="success default" v-if="pageStatus !== 'show'">
                    <i class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <!--<b-button v-if="siteForm.status === 'inactive' && pageStatus !== 'create'" @click="onAction('activate',siteForm)" size="sm" variant="success default">
                    <i class="icofont-check-circled"></i> {{$t('system-setting.status-active')}}
                  </b-button>-->
                  <b-button @click="onAction('delete',siteForm)" size="sm" variant="danger default"
                            v-if="pageStatus !== 'create'">
                    <i class="icofont-bin"></i> {{$t('system-setting.delete')}}
                  </b-button>
                  <b-button @click="onAction('list')" size="sm" variant="info default">
                    <i class="icofont-long-arrow-left"></i> {{$t('system-setting.return')}}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <div class="position-absolute" style="bottom: 2%;left: 2%">
              <img src="../../../assets/img/no_active_stamp.png">
            </div>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus === 'show'" class="h-100">
          <b-col cols="12 d-flex flex-column form-section " class="position-relative">
            <b-row>
              <b-col cols="6">
                <b-row>
                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldSerial"
                                    :placeholder="$t('system-setting.please-enter-site-no')"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="siteForm.fieldDesignation"
                                    :placeholder="$t('system-setting.please-enter-site-name')"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site-no')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-input type="text" v-model="selectedParentSerial" disabled></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.super-site')}}&nbsp;
                        <span class="text-danger">*</span>
                      </template>
                      <b-form-select v-if="siteForm.parentFieldId !=0" :options="superSiteOptions"
                                     v-model="siteForm.parentFieldId" disabled plain/>
                      <b-form-input v-else-if="siteForm.parentFieldId == 0" v-model="selectedParentSerial" type="text"
                                    disabled></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.manager')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.leader"
                                    :placeholder="$t('system-setting.please-enter-manager')"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group>
                      <template slot="label">
                        {{$t('system-setting.system-phone')}}
                      </template>
                      <b-form-input type="text" v-model="siteForm.mobile" :placeholder="''"></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="6">
                    <b-form-group :label="$t('system-setting.remarks')">
                      <b-form-textarea rows="4" v-model="siteForm.note"
                                       :placeholder="$t('system-setting.please-enter-remarks')"></b-form-textarea>
                    </b-form-group>
                  </b-col>

                </b-row>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1 align-items-end">
              <b-col cols="12" class="d-flex justify-content-end">
                <div class="mr-3">
                  <b-button v-if="siteForm.status === 'active' && siteForm.parentFieldId !=0" @click="onAction('inactivate',siteForm)" size="sm"
                            variant="warning default">
                    <i class="icofont-ban"></i> {{$t('system-setting.status-inactive')}}
                  </b-button>
                  <b-button v-if="siteForm.status === 'inactive'" @click="onAction('activate',siteForm)" size="sm" variant="success default">
                    <i class="icofont-check-circled"></i> {{$t('system-setting.status-active')}}
                  </b-button>
                  <b-button v-if="siteForm.status === 'inactive'" @click="onAction('delete',siteForm)" size="sm"
                            variant="danger default">
                    <i class="icofont-bin"></i> {{$t('system-setting.delete')}}
                  </b-button>
                  <b-button @click="onAction('list')" size="sm" variant="info default">
                    <i class="icofont-long-arrow-left"></i> {{$t('system-setting.return')}}
                  </b-button>
                </div>
              </b-col>
            </b-row>
            <div class="position-absolute" style="bottom: 2%;left: 2%">
              <img v-if="siteForm.status === 'inactive'" src="../../../assets/img/no_active_stamp.png">
              <img v-else-if="siteForm.status === 'active'" src="../../../assets/img/active_stamp.png">
            </div>
          </b-col>
        </b-row>
      </b-tab>
      <b-tab :title="$t('system-setting.site-architecture')">
        <b-row>
          <b-col cols="12">
            <div class="text-center">
              <vue2-org-tree
                :data="treeData"
                :horizontal="false"
                :collapsable="false"
                :label-class-name="treeLabelClass"
                :render-content="renderTreeContent"
              />
            </div>
          </b-col>
        </b-row>
      </b-tab>

    </b-tabs>
    <b-modal centered id="modal-inactive" ref="modal-inactive" :title="$t('system-setting.prompt')">
      {{$t('site-management.make-inactive-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="updateItemStatus('inactive')" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-inactive')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-delete" ref="modal-delete" :title="$t('system-setting.prompt')">
      {{$t('site-management.delete-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="removeItem()" class="mr-1">
          {{$t('system-setting.ok')}}
        </b-button>
        <b-button variant="danger" @click="hideModal('modal-delete')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>
  </div>
</template>


<script>
  import _ from 'lodash';
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
  import Vue2OrgTree from '../../../components/vue2-org-tree'
  import 'vue-select/dist/vue-select.css'
  import {validationMixin} from 'vuelidate';

  import {apiBaseUrl} from "../../../constants/config";
  import {getApiManager} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';

  const {required} = require('vuelidate/lib/validators');

  let getParentSerialName = (siteData, fieldId) => {
    let parentSerialNumber = null;
    if (siteData == null || siteData.length === 0)
      return parentSerialNumber;
    siteData.forEach(siteItem => {
      if (siteItem.fieldId === fieldId)
        parentSerialNumber = siteItem.fieldSerial;
    });
    return parentSerialNumber;
  };

  export default {
    components: {
      'vuetable': Vuetable,
      'vuetable-pagination': VuetablePagination,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      Vue2OrgTree
    },
    mixins: [validationMixin],
    mounted() {
      ///////////////////////////////////////////////////////////
      ////////////// Load site list from server /////////////////
      ///////////////////////////////////////////////////////////
      this.getSiteData();

      this.$refs.vuetable.$parent.transform = this.transformSiteTable.bind(this);
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },
      'siteForm.parentFieldId': function (newVal) {
        this.selectedParentSerial = getParentSerialName(this.siteData, newVal);
        if (this.selectedParentSerial === null)
          this.selectedParentSerial = this.$t('system-setting.none');
      },
      siteData:function (newVal,oldVal) {
        console.log(newVal);
        this.superSiteOptions = [];
        this.superSiteOptions = newVal.map(site => ({
          text: site.fieldDesignation,
          value: site.fieldId
        }));
        if (this.superSiteOptions.length === 0)
          this.superSiteOptions.push({
            text: this.$t('system-setting.none'),
            value: 0
          });
      }
    },
    data() {
      return {
        selectedSiteItem: null,
        siteData: [],
        selectedParentSerial: '',
        filterOption: {
          fieldDesignation: '',
          status: null,
          parentFieldDesignation: ''
        },
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/site-management/field/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'fieldId',
              sortField: 'fieldId',
              title: this.$t('system-setting.no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:fieldSerial',
              sortField: 'fieldSerial',
              title: this.$t('system-setting.site-no'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'fieldDesignation',
              sortField: 'fieldDesignation',
              title: this.$t('system-setting.site'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              sortField: 'status',
              title: this.$t('system-setting.status-active'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "active": `<span class="text-success">${this.$t('system-setting.status-active')}</span>`,
                  "inactive": `<span class="text-muted">${this.$t('system-setting.status-inactive')}</span>`
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              }
            },
            {
              name: 'parentFieldSerial',
              title: this.$t('system-setting.super-site-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                if (value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'parentFieldDesignation',
              title: this.$t('system-setting.super-site'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                if (value) {
                  return value;
                } else {
                  return this.$t('system-setting.none');
                }
              }
            },
            {
              name: 'leader',
              sortField: 'leader',
              title: this.$t('system-setting.manager'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'mobile',
              sortField: 'mobile',
              title: this.$t('system-setting.system-phone'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'note',
              sortField: 'note',
              title: this.$t('system-setting.remarks'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:operating',
              title: this.$t('system-setting.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '210px'
            }
          ]
        },
        stateOptions: [
          {value: null, text: this.$t('system-setting.status-all')},
          {value: "active", text: this.$t('system-setting.status-active')},
          {value: "inactive", text: this.$t('system-setting.status-inactive')},
        ],
        pageStatus: 'table', // table, create, edit, show
        selectedSite: '0000',
        superSiteOptions: [],
        treeData: {
          id: 0,
          label: '0000 首都机场',
          children: [
            {
              id: 1,
              label: '0100 1号航站楼'
            },
            {
              id: 2,
              label: '0200 2号航站楼',
              children: [
                {
                  id: 3,
                  label: '0201 通道1'
                },
                {
                  id: 4,
                  label: '0202 通道2'
                }
              ]
            },
            {
              id: 5,
              label: '0300 3号航站楼',
              children: [
                {
                  id: 6,
                  label: '0301 通道001'
                }
              ]
            }
          ]
        },
        siteForm: {
          fieldId: 0,
          fieldSerial: '',
          fieldDesignation: '',
          parentFieldId: '',
          parentFieldDesignation: '',
          leader: '',
          mobile: '',
          note: ''
        }
      }
    },
    validations: {
      siteForm: {
        fieldSerial: {
          required
        },
        fieldDesignation: {
          required
        },
        parentFieldId: {
          required
        }
      }
    },
    methods: {
      getSiteData(){
        getApiManager().post(`${apiBaseUrl}/site-management/field/get-all`).then((response) => {
          let message = response.data.message;
          let data = response.data.data;
          switch (message) {
            case responseMessages['ok']:
              this.siteData = data;
              break;
          }
        });
      },
      onAction(value, data = null) {
        switch (value) {
          case 'create':
            this.initialize();
            this.pageStatus = 'create';
            break;
          case 'save':
            this.saveSiteItem();
            break;
          case 'edit':
            this.initialize(data);
            this.pageStatus = 'edit';
            break;
          case 'activate':
            this.siteForm = data;
            this.updateItemStatus('active');
            break;
          case 'show':
            this.initialize(data);
            this.pageStatus = 'show';
            break;
          case 'inactivate':
            this.initialize(data);
            this.$refs['modal-inactive'].show();
            break;
          case 'delete':
            this.initialize(data);
            this.$refs['modal-delete'].show();
            break;
          case 'list':
            this.pageStatus = 'table';
            break;
        }
      },
      initialize(data = null) {
        if (data == null) {
          this.siteForm = {
            fieldSerial: '',
            fieldDesignation: '',
            parentFieldId: '',
            parentFieldDesignation: '',
            leader: '',
            mobile: '',
            note: '',
            fieldId: 0
          };
          this.selectedSiteItem = '';
        } else {
          this.siteForm = data;
          this.selectedSiteItem = getParentSerialName(this.siteData, this.siteForm.parentFieldId);
        }

      },
      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filterOption = {
          fieldDesignation: '',
          status: null,
          parentFieldDesignation: ''
        };
        this.$refs.vuetable.refresh();
      },
      /**
       * save new site item
       */
      saveSiteItem() {
        //this.submitted = true;
        this.$v.siteForm.$touch();
        if (this.$v.siteForm.$invalid) {
          return;
        }
        let finalLink = this.siteForm.fieldId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/` + finalLink, this.siteForm)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.site-added-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'table';
                this.getSiteData();
                break;
            }
          })
          .catch((error) => {
          });
      },


      siteTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          filter: this.filterOption
        });
      },
      transformSiteTable(response) {
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
          temp.parentFieldSerial = temp.parent != null ? temp.parent.fieldSerial : null;
          temp.parentFieldDesignation = temp.parent != null ? temp.parent.fieldDesignation : null;
          transformed.data.push(temp);
        }

        return transformed

      },
      onPaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
      },
      onChangePage(page) {
        this.$refs.vuetable.changePage(page);
      },

      updateItemStatus(statusValue) {
        let fieldId = this.siteForm.fieldId;
        if (fieldId === 0)
          return false;

        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/update-status`, {
            fieldId: fieldId,
            status: statusValue
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.status-updated-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                if (this.siteForm.fieldId > 0)
                  this.siteForm.status = statusValue;
                if (this.pageStatus === 'table')
                  this.$refs.vuetable.refresh();
                break;

            }
          })
          .catch((error) => {
          });
        this.$refs['modal-inactive'].hide();
      },

      removeItem() {
        let fieldId = this.siteForm.fieldId;
        if (fieldId === 0)
          return false;
        getApiManager()
          .post(`${apiBaseUrl}/site-management/field/delete`, {
            fieldId: fieldId,
          })
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`site-management.site-deleted-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.pageStatus = 'table';
                if (this.siteForm.fieldId > 0)
                  this.siteForm = null;
                this.$refs.vuetable.refresh();
                this.getSiteData();
                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`site-management.site-has-children`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
        this.$refs['modal-delete'].hide();
      },

      hideModal(modal) {
        this.$refs[modal].hide();
      },
      renderTreeContent: function (h, data) {
        return data.label;
      },
      treeLabelClass: function (data) {
        const labelClasses = ['bg-primary', 'bg-secondary', 'bg-success', 'bg-info', 'bg-warning', 'bg-danger'];
        return `${labelClasses[data.id % 6]} text-white`;
      }
    }
  }
</script>
