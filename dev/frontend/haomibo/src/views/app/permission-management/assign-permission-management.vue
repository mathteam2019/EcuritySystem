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
        <b-row v-if="pageStatus==='table'">
          <b-col cols="7">
            <b-card class="mb-4">

              <b-row>
                <b-col cols="5">
                  <b-form-group :label="$t('permission-management.assign-permission-management.assign-flag')">
                    <b-form-select :options="assignFlagSelectOptions" v-model="filter.assignFlag" plain/>
                  </b-form-group>
                </b-col>

                <b-col cols="7">
                  <b-form-group class="search-form-group">
                    <template slot="label">&nbsp;</template>
                    <b-form-input :placeholder="$t('permission-management.assign-permission-management.please-input-user-name')" v-model="filter.userName"></b-form-input>

                    <i class="search-input-icon simple-icon-magnifier"></i>

                  </b-form-group>
                </b-col>
              </b-row>

              <b-row>
                <b-col >
                  <vuetable
                    ref="vuetable"
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :http-fetch="vuetableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"

                    @vuetable:pagination-data="onPaginationData"
                  >

                    <template slot="actions" slot-scope="props">
                      <div>
                        <template v-if="props.rowData.status=='inactive'">
                          <b-button
                            size="sm"
                            variant="info"
                            @click="onAction('modify', props.rowData, props.rowIndex)">
                            {{ $t('permission-management.org-action-modify') }}
                          </b-button>
                          <b-button
                            size="sm"
                            variant="success"
                            @click="onAction('activate', props.rowData, props.rowIndex)">
                            {{ $t('permission-management.org-action-activate') }}
                          </b-button>
                          <b-button
                            size="sm"
                            variant="danger"
                            @click="onAction('delete', props.rowData, props.rowIndex)">
                            {{ $t('permission-management.org-action-delete') }}
                          </b-button>
                        </template>

                        <template v-if="props.rowData.status=='active'">
                          <b-button
                            size="sm"
                            variant="info"
                            disabled>
                            {{ $t('permission-management.org-action-modify') }}
                          </b-button>

                          <template v-if="props.rowData.parentOrgId==0">
                            <b-button
                              size="sm"
                              variant="warning"
                              disabled>
                              {{ $t('permission-management.org-action-deactivate') }}
                            </b-button>
                          </template>
                          <template v-else>
                            <b-button
                              size="sm"
                              variant="warning"
                              @click="onAction('deactivate', props.rowData, props.rowIndex)">
                              {{ $t('permission-management.org-action-deactivate') }}
                            </b-button>
                          </template>


                          <b-button
                            size="sm"
                            variant="danger"
                            disabled>
                            {{ $t('permission-management.org-action-delete') }}
                          </b-button>
                        </template>

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
          <b-col cols="5">
            <b-card :title="'TODO'">
              <h1>Hello world</h1>
            </b-card>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus==='create'">
          <b-col >
            <b-card class="mb-4">
              <b-row>
                <b-col cols="6">

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text"
                                      v-model="createPage.orgName"
                                      :placeholder="$t('permission-management.please-enter-organization-name')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text"
                                      v-model="createPage.orgNumber"
                                      :placeholder="$t('permission-management.please-enter-organization-number')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select :options="parentOrganizationNameSelectOptions"
                                       v-model="createPage.parentOrgId" plain/>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" disabled v-model="createPageSelectedParentOrganizationNumber"
                                      :placeholder="$t('permission-management.please-select-parent-organization')"/>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                        <b-form-input type="text"
                                      v-model="createPage.leader"
                                      :placeholder="$t('permission-management.please-enter-organization-leader')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                        <b-form-input type="text"
                                      v-model="createPage.mobile"
                                      :placeholder="$t('permission-management.please-enter-organization-mobile')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-note')}}</template>

                        <b-form-textarea
                          v-model="createPage.note"
                          :placeholder="$t('permission-management.please-enter-organization-note')"
                          :rows="3"
                          :max-rows="6"/>

                      </b-form-group>
                    </b-col>
                  </b-row>


                </b-col>
              </b-row>
              <b-row class="mb-3">
                <b-col  class="text-right">
                  <b-button variant="primary" @click="onCreatePageSaveButton()">{{
                    $t('permission-management.save-button') }}
                  </b-button>
                  <b-button variant="secondary" @click="onCreatePageBackButton()">{{
                    $t('permission-management.back-button') }}
                  </b-button>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus==='modify'">
          <b-col >
            <b-card class="mb-4">
              <b-row>
                <b-col cols="6">

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-name')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text"
                                      v-model="modifyPage.orgName"
                                      :placeholder="$t('permission-management.please-enter-organization-name')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text"
                                      v-model="modifyPage.orgNumber"
                                      :placeholder="$t('permission-management.please-enter-organization-number')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.parent-organization-name')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select :options="parentOrganizationNameSelectOptions"
                                       v-model="modifyPage.parentOrgId" plain/>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.parent-organization-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" disabled v-model="modifyPageSelectedParentOrganizationNumber"
                                      :placeholder="$t('permission-management.please-select-parent-organization')"/>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-leader')}}</template>
                        <b-form-input type="text"
                                      v-model="modifyPage.leader"
                                      :placeholder="$t('permission-management.please-enter-organization-leader')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-mobile')}}</template>
                        <b-form-input type="text"
                                      v-model="modifyPage.mobile"
                                      :placeholder="$t('permission-management.please-enter-organization-mobile')"></b-form-input>
                      </b-form-group>
                    </b-col>
                  </b-row>

                  <b-row class="mb-3">
                    <b-col >
                      <b-form-group label-cols="4" horizontal>
                        <template slot="label">{{$t('permission-management.organization-note')}}</template>

                        <b-form-textarea
                          v-model="modifyPage.note"
                          :placeholder="$t('permission-management.please-enter-organization-note')"
                          :rows="3"
                          :max-rows="6"/>

                      </b-form-group>
                    </b-col>
                  </b-row>


                </b-col>
              </b-row>
              <b-row class="mb-3">
                <b-col  class="text-right">
                  <b-button variant="primary" @click="onModifyPageSaveButton()">{{
                    $t('permission-management.save-button') }}
                  </b-button>
                  <b-button variant="secondary" @click="onModifyPageBackButton()">{{
                    $t('permission-management.back-button') }}
                  </b-button>
                </b-col>
              </b-row>
            </b-card>
          </b-col>
        </b-row>

      </b-tab>

      <b-tab :title="$t('permission-management.assign-permission-management.assign-to-group')">
        <b-row v-if="groupPageStatus==='table'">
          <b-col cols="12">
            <div class="p-2">

              <b-row>
                <b-col cols="6">
                  <b-row>

                    <b-col>
                      <b-form-group :label="$t('permission-management.assign-permission-management.group.user-group')">
                        <b-form-input v-model="groupFilter.groupName"></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.assign-permission-management.group.groupMember')">
                        <b-form-input v-model="groupFilter.userName" ></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.assign-permission-management.group.role')">
                        <b-form-input v-model="groupFilter.role" ></b-form-input>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.assign-permission-management.group.data-range')">
                        <b-form-input v-model="groupFilter.dataRange" ></b-form-input>
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

              <b-row>
                <b-col cols="12">
                  <vuetable
                    ref="userGroupTable"
                    :api-url="userGroupTableItems.apiUrl"
                    :fields="userGroupTableItems.fields"
                    :http-fetch="userGroupTableHttpFetch"
                    pagination-path="userGroupPagination"
                    class="table-hover"
                    @vuetable:pagination-data="onUserGroupTablePaginationData"
                  >
                    <template slot="userNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show', props.rowData, props.rowIndex)">{{ props.rowData.userNumber }}</span>
                    </template>
                    <template slot="actions" slot-scope="props">
                      <div >

                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="primary default btn-square"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='inactive'"
                          size="sm"
                          variant="primary default btn-square"
                          disabled>
                          <i class="icofont-edit"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="success default btn-square"
                          @click="onAction('active', props.rowData, props.rowIndex)">
                          <i class="icofont-check-circled"></i>
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='active'"
                          size="sm"
                          variant="warning default btn-square"
                          @click="onAction('inactive', props.rowData, props.rowIndex)">
                          <i class="icofont-ban"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='inactive' && props.rowData.status!='active'"
                          size="sm"
                          variant="success default btn-square"
                          disabled>
                          <i class="icofont-ban"></i>
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='inactive'"
                          size="sm"
                          variant="danger default btn-square"
                          @click="onAction('blocked', props.rowData, props.rowIndex)">
                          <i class="icofont-minus-circle"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status=='blocked'"
                          size="sm"
                          variant="success default btn-square"
                          @click="onAction('unblock', props.rowData, props.rowIndex)">
                          <i class="icofont-power"></i>
                        </b-button>


                        <b-button
                          v-if="props.rowData.status!='inactive' && props.rowData.status!='blocked'"
                          size="sm"
                          variant="danger default btn-square"
                          disabled>
                          <i class="icofont-minus-circle"></i>
                        </b-button>


                        <b-button
                          v-if="props.rowData.status=='pending'"
                          size="sm"
                          variant="purple default btn-square"
                          @click="onAction('reset-password', props.rowData, props.rowIndex)">
                          <i class="icofont-ui-password"></i>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!='pending'"
                          size="sm"
                          variant="purple default btn-square"
                          disabled>
                          <i class="icofont-ui-password"></i>
                        </b-button>


                      </div>
                    </template>

                  </vuetable>
                  <vuetable-pagination-bootstrap
                    ref="userGroupPagination"
                    @vuetable-pagination:change-page="onUserGroupTableChangePage"
                    :initial-per-page="userGroupTableItems.perPage"
                    @onUpdatePerPage="userGroupTableItems.perPage = Number($event)"
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
                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>
        <b-row v-else-if="groupPageStatus==='create'">
          <b-col cols="12" class="form-section">
            <b-row>
              <b-col cols="4">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.user-group')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select v-model="groupForm.userGroup" :options="groupUserGroupOptions" plain />
                </b-form-group>
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.member')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <label class=""></label>
                </b-form-group>
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.role')}}&nbsp;<span
                    class="text-danger">*</span></template>

                  <v-select v-model="groupForm.role" multiple :options="roleOptions" :dir="direction"/>

                </b-form-group>
                <b-form-group>
                  <template slot="label">{{$t('permission-management.assign-permission-management.group.data-range')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <div class="d-flex ">
                    <div>
                      <b-form-radio-group  stacked>
                        <b-form-radio value="first">{{$t('permission-management.assign-permission-management.group.one-user-data')}}</b-form-radio>
                        <b-form-radio value="second">{{$t('permission-management.assign-permission-management.group.group-user-data')}}</b-form-radio>
                        <b-form-radio value="third">{{$t('permission-management.assign-permission-management.group.all-user-data')}}</b-form-radio>
                        <b-form-radio value="four">{{$t('permission-management.assign-permission-management.group.select-data-group')}}</b-form-radio>
                      </b-form-radio-group>
                    </div>
                    <div class="align-self-end flex-grow-1 pl-2">
                      <b-form-select v-model="groupForm.filterGroup" :options="filterGroupOptions" plain/>
                    </div>
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="12 text-right">
                <b-button variant="info default" @click="onActionGroup('save-item')"><i class="icofont-save"></i> {{$t('permission-management.save')}}</b-button>
                <b-button variant="danger default" @click="onActionGroup('delete-item')"><i class="icofont-bin"></i> {{$t('permission-management.delete')}}</b-button>
                <b-button variant="info default" @click="onActionGroup('show-list')"><i class="icofont-long-arrow-left"></i> {{$t('permission-management.return')}}</b-button>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
      </b-tab>


    </b-tabs>


  </div>
</template>
<script>

  import {apiBaseUrl} from '../../../constants/config';
  import Vuetable from 'vuetable-2/src/components/Vuetable'
  import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap';
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../../utils";

  import Vue2OrgTree from 'vue2-org-tree'
  import {getApiManager} from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';

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
      'v-select': vSelect,
      'vuetable': Vuetable,
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      Vue2OrgTree
    },
    mounted() {

      this.$refs.vuetable.$parent.transform = this.transform.bind(this);

      getApiManager().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`,{
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
        direction: getDirection().direction,
        assignFlagSelectOptions: [ // on the filtering
          {value: null, text: this.$t('permission-management.assign-permission-management.assign-flag-select-options.all')},
          {value: 'assigned', text: this.$t('permission-management.assign-permission-management.assign-flag-select-options.assigned')},
          {value: 'not_assigned', text: this.$t('permission-management.assign-permission-management.assign-flag-select-options.not-assigned')}
        ],
        filter: {
          assignFlag: null,
          userName: ''
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


        parentOrganizationNameSelectOptions: {}, // this is used for both create and modify pages, parent org select box options
        vuetableItems: { // main table options
          apiUrl: `${apiBaseUrl}/permission-management/organization-management/organization/get-by-filter-and-page`,
          fields: [
            {
              name: 'orgId',
              title: this.$t('permission-management.th-no'),
              sortField: 'orgId',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'orgNumber',
              title: this.$t('permission-management.th-org-number'),
              sortField: 'orgNumber',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'orgName',
              title: this.$t('permission-management.th-org-name'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-org-status'),
              sortField: 'status',
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {

                const dictionary = {
                  'active': `<span class="text-success">${this.$t('permission-management.org-status-active')}</span>`,
                  'inactive': `<span class="text-dark">${this.$t('permission-management.org-status-inactive')}</span>`,
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
              callback: (value) => {

                return value ? value.orgNumber : this.$t('permission-management.org-none');

              }
            },
            {
              name: 'parent',
              title: this.$t('permission-management.th-org-parent-org-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {

                return value ? value.orgName : this.$t('permission-management.org-none');

              }
            },
            {
              name: 'leader',
              title: this.$t('permission-management.th-org-leader'),
              sortField: 'leader',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'mobile',
              title: this.$t('permission-management.th-org-mobile'),
              titleClass: 'text-center',
              dataClass: 'text-center'
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
              dataClass: 'text-center'
            },

          ],
          perPage: 5,
        },
        treeData: { // holds tree data for org diagram
        },

        //TODO assign permission management for user group part
        groupForm:{
          userGroup:null,
          role:null,
        },
        groupUserGroupOptions:[
          '组1',
          '组2',
          '组3',
        ],
        roleOptions:[
          '角色1',
          '角色2',
          '角色3',
        ],
        filterGroupOptions:[
          '组1',
          '组2',
          '组3',
        ],
        groupPageStatus:'table', //table, create
        groupFilter:{
          groupName:null,
          userName:null,
          role:null,
          dataRange:null,
          filterGroup:null
        },
        userGroupTableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/user-group/get-by-filter-and-page`,
          perPage: 5,
          fields: [
            {
              name: 'userGroupId',
              title: this.$t('permission-management.th-no'),
              sortField: 'userGroupId',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:userGroupNumber',
              title: this.$t('permission-management.user.user-group-number'),
              sortField: 'groupNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'groupName',
              title: this.$t('permission-management.user.user-group-name'),
              sortField: 'userGroupName',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.user.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center'
            }
          ],
        },

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

        this.parentOrganizationNameSelectOptions = selectOptions;

      }
    },
    methods: {

      onSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onResetButton() {
        this.filter = {
          orgName: '',
          status: null,
          parentOrgName: ''
        };
        this.$refs.vuetable.refresh();
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
      onAction(action, data, index) { // called when any action button is called from table

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

        };

        let deleteItem = () => {
          this.selectedOrg = data;
          this.$refs['modal-delete'].show();
        };

        let activateItem = () => {

          // call api
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/update-organization-status`, {
              'orgId': data.orgId,
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

                  this.$refs.vuetable.refresh();

                  break;
              }
            })
            .catch((error) => {
            });


        };

        let deactivateItem = () => {
          this.selectedOrg = data;
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
        return data.label;
      },
      treeLabelClass: function (data) {
        const labelClasses = ['bg-primary', 'bg-secondary', 'bg-success', 'bg-info', 'bg-warning', 'bg-danger'];
        return `${labelClasses[data.id % 6]} text-white`;
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
      },
      onCreatePageSaveButton() { // save button is clicked from create page

        // validate inputs
        if (this.createPage.orgName == '') {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-name`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        if (this.createPage.orgNumber == '') {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-number`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        if (this.createPage.parentOrgId == null) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-select-parent-organization`), {
            duration: 3000,
            permanent: false
          });
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

        // validate inputs

        if (this.modifyPage.orgName == '') {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-name`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        if (this.modifyPage.orgNumber == '') {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-number`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        if (this.modifyPage.parentOrgId == null) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-select-parent-organization`), {
            duration: 3000,
            permanent: false
          });
          return;
        }

        if (this.modifyPage.parentOrgId == this.modifyPage.selectedOrg.orgId) {
          this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-select-different-parent-organization`), {
            duration: 3000,
            permanent: false
          });
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

                this.$refs.vuetable.refresh();

                break;
              case responseMessages["has-children"]: // has children
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.organization-has-children`), {
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

      //TODO assign user group point
      onActionGroup(value){
        console.log(value);
        switch (value) {
          case 'show-list':
            this.groupPageStatus = 'table';
            break;
          case 'delete-item':
            break;
        }
      },
      onAssignUserGroupSearchButton() {
        this.$refs.vuetable.refresh();
      },
      onAssignUserGroupResetButton() {
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

      onAssignUserGroupCreatePage(){
        this.groupPageStatus = 'create';
      },
      userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server

        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userGroupTableItems.perPage,
          filter: {
            groupName: this.groupFilter.name,
          }
        });
      },
      onUserGroupTablePaginationData(paginationData) {
        this.$refs.userGroupPagination.setPaginationData(paginationData)
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
      fnTransformUserGroupTable(response) {
        this.selectedUserGroupItem = null;
        let transformed = {};

        let data = response.data;

        transformed.userGroupPagination = {
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


</style>
