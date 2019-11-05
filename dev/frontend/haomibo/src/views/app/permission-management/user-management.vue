<style>
  .badge-bottom-left {
    left: -7px;
    bottom: 0px;
  }

  span.cursor-p {
    cursor: pointer !important;
  }
  .h-30vh {
    height: 30vh;
    max-height: 30vh;
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

  .selected-row {
    background-color: #0000ff20 !important;
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

      <b-tab :title="$t('permission-management.member-table')">
        <b-row v-if="pageStatus=='table'">
          <b-col cols="12">
            <div class="mb-4">

              <b-row>
                <b-col cols="6">
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
                      <b-form-group :label="$t('permission-management.user-category')">
                        <b-form-select v-model="filter.category" :options="categorySelectData" plain/>
                      </b-form-group>
                    </b-col>

                    <b-col>
                      <b-form-group :label="$t('permission-management.affiliated-institution')">
                        <b-form-select v-model="filter.orgId"
                                       :options="orgNameSelectData"
                                       plain/>
                      </b-form-group>
                    </b-col>
                  </b-row>
                </b-col>
                <b-col cols="6" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                      <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                      <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default">
                      <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default">
                      <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onCreatePage()" variant="success default">
                      <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
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
                    :http-fetch="userTableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:pagination-data="onUserTablePaginationData"
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
                    ref="pagination"
                    @vuetable-pagination:change-page="onUserTableChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  ></vuetable-pagination-bootstrap>

                  <b-modal ref="modal-prompt" :title="$t('permission-management.prompt')">
                    {{promptTemp.action==='blocked'?$t('permission-management.user.block-prompt'):$t('permission-management.user.inactive-prompt')}}
                    <template slot="modal-footer">
                      <b-button variant="primary" @click="fnChangeItemStatus()" class="mr-1">
                        {{$t('permission-management.modal-ok')}}
                      </b-button>
                      <b-button variant="danger" @click="fnHideModal('modal-prompt')">
                        {{$t('permission-management.modal-cancel')}}
                      </b-button>
                    </template>
                  </b-modal>
                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus=='create'">
          <b-col cols="12">
            <div class="mb-4">
              <b-row>
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
                        <b-form-radio-group v-model="profileForm.category" inline>
                          <b-form-radio value="admin">
                            {{$t('permission-management.admin')}}
                          </b-form-radio>
                          <b-form-radio value="normal">
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
                        </div>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.password')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <div class="d-flex ">
                          <div>
                            <b-form-radio-group v-model="profileForm.passwordType" stacked>
                              <b-form-radio value="default" class="pb-2">
                                {{$t('permission-management.password-basic')}}
                              </b-form-radio>
                              <b-form-radio value="other" class="pb-2">
                                {{$t('permission-management.password-other')}}
                              </b-form-radio>
                            </b-form-radio-group>
                          </div>
                          <div class="align-self-end flex-grow-1 pl-3">
                            <b-form-input type="password" v-model="profileForm.passwordValue"
                                          :disabled="profileForm.passwordType==='default'"
                                          :placeholder="$t('permission-management.please-enter-password')"></b-form-input>
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

                </b-col>
                <b-col cols="2" class="text-right">
                  <b-card class="mb-4" no-body>
                    <div class="position-relative img-wrapper p-1" style="min-height: 180px">
                      <img :src="profileForm.avatar" onerror="src='\\assets\\img\\profile.jpg'" class="card-img-top"/>
                      <b-badge
                        :variant="profileForm.status === 'active' ? 'success' : profileForm.status === 'inactive' ? 'light':profileForm.status === 'pending' ? 'primary':'danger'"
                        pill class="position-absolute badge-bottom-left">
                        {{$t('permission-management.' + profileForm.status)}}
                      </b-badge>
                    </div>
                    <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
                  </b-card>
                  <b-button @click="$refs.profileFile.click()" class="mb-1" variant="light default" size="sm">{{
                    $t('permission-management.upload-image')}}
                  </b-button>
                </b-col>
                <b-col cols="12">
                  <b-row>
                    <b-col cols="12" class="text-right">
                      <b-button class="mb-1" @click="onSaveUserPage()" variant="info default">{{
                        $t('permission-management.save') }}
                      </b-button>
                      <b-button class="mb-1" @click="onTableListPage()" variant="danger default">{{
                        $t('permission-management.return') }}
                      </b-button>
                    </b-col>
                  </b-row>
                </b-col>
              </b-row>
            </div>
          </b-col>
        </b-row>
        <b-row v-if="pageStatus=='show'">
          <b-col cols="12">
            <b-card class="mb-4">
              <b-row>
                <b-col cols="10">
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userName"
                                      :placeholder="$t('permission-management.please-enter-user-name')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userNumber"

                                      :placeholder="$t('permission-management.please-enter-user-id')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select v-model="profileForm.gender" :options="genderOptions" plain
                        />
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.license-number')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.identityCard"
                                      :placeholder="$t('permission-management.please-enter-license-number')"></b-form-input>

                      </b-form-group>
                    </b-col>
                  </b-row>
                  <b-row class="mb-2">
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-select v-model="profileForm.orgId" :options="orgNameSelectData" plain
                        />
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
                        <b-form-input type="email" v-model="profileForm.email"
                                      :placeholder="$t('permission-management.please-enter-email')"></b-form-input>
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
                        <b-form-radio-group v-model="profileForm.category" inline>
                          <b-form-radio value="admin">
                            {{$t('permission-management.admin')}}
                          </b-form-radio>
                          <b-form-radio value="normal">
                            {{$t('permission-management.normal')}}
                          </b-form-radio>
                        </b-form-radio-group>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <b-form-input type="text" v-model="profileForm.userAccount"
                                      :placeholder="$t('permission-management.please-enter-user-account')"></b-form-input>
                      </b-form-group>
                    </b-col>
                    <b-col cols="3">
                      <b-form-group>
                        <template slot="label">{{$t('permission-management.password')}}&nbsp;<span
                          class="text-danger">*</span></template>
                        <div class="d-flex ">
                          <div>
                            <b-form-radio-group v-model="profileForm.passwordType" stacked>
                              <b-form-radio value="default">
                                {{$t('permission-management.password-basic')}}
                              </b-form-radio>
                              <b-form-radio value="other">
                                {{$t('permission-management.password-other')}}
                              </b-form-radio>
                            </b-form-radio-group>
                          </div>
                          <div class="align-self-end flex-grow-1 pl-2">
                            <b-form-input type="password" v-model="profileForm.passwordValue"
                                          :disabled="profileForm.passwordType==='default'"
                                          :placeholder="$t('permission-management.please-enter-password')"></b-form-input>
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

                </b-col>
                <b-col cols="2" class="text-right">
                  <b-card class="mb-4" no-body>
                    <div class="position-relative img-wrapper p-1" style="min-height: 180px">
                      <img :src="profileForm.avatar" onerror="src='\\assets\\img\\profile.jpg'" class="card-img-top"/>
                      <b-badge
                        :variant="profileForm.status === 'active' ? 'success' : profileForm.status === 'inactive' ? 'light':profileForm.status === 'pending' ? 'primary':'danger'"
                        pill class="position-absolute badge-bottom-left">
                        {{$t('permission-management.' + profileForm.status)}}
                      </b-badge>
                    </div>
                    <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
                  </b-card>
                </b-col>
                <b-col cols="12">
                  <b-row>
                    <b-col cols="12">
                      <b-button class="mb-1" @click="onTableListPage()" variant="danger default">{{
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
          <b-col cols="8">
            <div class="section">
              <b-row>
                <b-col cols="3" class="pr-3">
                  <b-form-group class="search-form-group">
                    <template slot="label">{{$t('permission-management.user.user-group-name')}}</template>
                    <b-form-input :placeholder="$t('permission-management.user.please-enter-group-name')"
                                  v-model="groupFilter.name"></b-form-input>
                    <i class="search-input-icon simple-icon-magnifier"></i>
                  </b-form-group>
                </b-col>
                <b-col cols="9" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onUserGroupSearchButton()">
                      <i class="icofont-search-1"></i>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onUserGroupResetButton()">
                      <i class="icofont-ui-reply"></i>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default">
                      <i class="icofont-share-alt"></i>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default">
                      <i class="icofont-printer"></i>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onUserGroupCreateButton()" variant="success default">
                      <i class="icofont-plus"></i>&nbsp;{{$t('permission-management.new') }}
                    </b-button>
                  </div>
                </b-col>
              </b-row>
              <b-row>
                <b-col cols="12" class="table-responsive">
                  <vuetable
                    ref="userGroupTable"
                    :api-url="userGroupTableItems.apiUrl"
                    :fields="userGroupTableItems.fields"
                    :http-fetch="userGroupTableHttpFetch"
                    pagination-path="userGroupPagination"
                    class="table-hover"
                    @vuetable:pagination-data="onUserGroupTablePaginationData"
                  >
                    <template slot="userGroupNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onUserGroupTableRowClick(props.rowData)">{{ props.rowData.groupNumber }}</span>
                    </template>
                    <template slot="operating" slot-scope="props">
                      <b-button variant="danger default btn-square" class="m-0" @click="onAction('group-remove', props.rowData, props.rowIndex)"><i class="icofont-bin"></i> </b-button>
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
          <b-col cols="4">
            <div class="section" v-if="selectedUserGroupItem">
              <b-row>
                <b-col cols="8" v-if="groupForm.status=='create'">
                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.user.group-number')}}&nbsp;
                      <span class="text-danger">*</span>
                    </template>
                    <b-form-input
                      v-model="groupForm.groupNumber"
                      :state="!$v.groupForm.groupNumber.$invalid" />
                    <div v-if="!$v.groupForm.groupNumber.$invalid">&nbsp;</div>
                    <b-form-invalid-feedback>{{$t('permission-management.user.required-field')}}
                    </b-form-invalid-feedback>

                  </b-form-group>

                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.user.group-name')}}&nbsp;
                      <span class="text-danger">*</span>
                    </template>
                    <b-form-input v-if="groupForm.status=='create'"
                      v-model="groupForm.groupName"
                      :state="!$v.groupForm.groupName.$invalid" />
                    <div v-if="!$v.groupForm.groupName.$invalid">&nbsp;</div>
                    <b-form-invalid-feedback>{{$t('permission-management.user.required-field')}}
                    </b-form-invalid-feedback>

                  </b-form-group>
                </b-col>
                <b-col cols="8" v-if="groupForm.status!='create'">
                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.user.group-number')}}&nbsp;
                      <span class="text-danger">*</span>
                    </template>
                   <label >
                     {{selectedUserGroupItem.groupNumber}}
                   </label>

                  </b-form-group>

                  <b-form-group>
                    <template slot="label">
                      {{$t('permission-management.user.group-name')}}&nbsp;
                      <span class="text-danger">*</span>
                    </template>
                    <label >
                      {{selectedUserGroupItem.groupName}}
                    </label>

                  </b-form-group>
                </b-col>
              </b-row>
              <b-row>
                <b-col cols="12">
                  <label class="font-weight-bold">{{$t('permission-management.user.group-member')}}<span class="text-danger">*</span></label>
                </b-col>
                <b-col class="text-right">
                  <b-form-group>
                    <b-form-checkbox v-model="isSelectedAllUsersForDataGroup">
                      {{$t('permission-management.permission-control.select-all')}}
                    </b-form-checkbox>
                  </b-form-group>
                </b-col>
              </b-row>

              <b-row>
                <b-col class="h-30vh">

                  <v-tree ref='orgUserTree' :data='orgUserTreeData' :multiple="true" :halfcheck='true'/>
                </b-col>
              </b-row>

              <b-row>
                <b-col cols="12" class="text-right pt-3" v-if="groupForm.status=='create'">
                  <b-form-group>
                    <b-button @click="onClickCreateUserGroup" variant="info default"><i class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                    </b-button>
                  </b-form-group>
                </b-col>
                <b-col cols="12" class="text-right pt-3" v-if="groupForm.status!='create'">
                  <b-form-group>
                    <b-button @click="onClickModifyUserGroup" variant="info default"><i class="icofont-save"></i> {{$t('permission-management.permission-control.save')}}
                    </b-button>
                    <b-button @click="onClickDeleteUserGroup" variant="danger default"><i class="icofont-bin"></i> {{$t('permission-management.delete')}}
                    </b-button>

                  </b-form-group>
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


  /**
   * getting orgFull name with parent org
   * @param orgData
   * @returns {*}
   */
  let fnGetOrgFullName = orgData => {
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
      'vuetable-pagination-bootstrap': VuetablePaginationBootstrap,
      'v-tree': VTree
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
          required,maxLength: maxLength(50)
        },
        category: {
          required
        },
      },
      groupForm: {
        groupName: {
          required
        },
        groupNumber: {
          required
        }
      }
    },
    mounted() {
      this.$refs.vuetable.$parent.transform = this.transform.bind(this);
      this.$refs.userGroupTable.$parent.transform = this.fnTransformUserGroupTable.bind(this);
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
      });
      getApiManager().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {
        type: 'with_org_tree'
      }).then((response) => {
        let message = response.data.message;
        let data = response.data.data;
        switch (message) {
          case responseMessages['ok']:
            this.userData = data;
            break;
        }
      })

    },
    data() {
      return {
        submitted: false,
        tableData: [],
        pageStatus: 'table',
        defaultOrgId: '',
        filter: {
          userName: '',
          status: null,
          orgId: '',
          category: null
        },
        promptTemp: {
          userId: 0,
          action: ''
        },
        orgData: [],
        userData: [],
        orgUserTreeData: [],
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
        },
        items: [
          {id: 1, first_name: 'Mark', last_name: 'Otto', username: '@mdo'},
          {id: 2, first_name: 'Jacob', last_name: 'Thornton', username: '@fat'},
          {id: 3, first_name: 'Lary', last_name: 'the Bird', username: '@twitter'}
        ],
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/user/get-by-filter-and-page`,
          fields: [
            {
              name: 'userId',
              title: this.$t('permission-management.th-no'),
              sortField: 'userId',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: '__slot:userNumber',
              title: this.$t('permission-management.th-user-id'),
              sortField: 'userNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'userName',
              title: this.$t('permission-management.th-username'),
              sortField: 'userName',
              titleClass: 'text-center',
              dataClass: 'text-center'
            },
            {
              name: 'gender',
              title: this.$t('permission-management.gender'),
              sortField: 'gender',
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
              titleClass: 'text-center btn-actions',
              dataClass: 'text-center btn-actions'
            },

          ],
          perPage: 5,
        },
        //second tab content
        selectedUserGroupItem: null,
        groupForm: {
          groupName: '',
          groupNumber: '',
          status:'create'
        },
        groupFilter: {
          name: ''
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

        isSelectedAllUsersForDataGroup: false,
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
      },
      'userGroupTableItems.perPage': function (newVal) {
        this.$refs.userGroupTable.refresh();
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
      onCreatePage() { // move to create page
        // reset models
        this.onInitialUserData();
        this.submitted = false;
        // change page to create
        this.pageStatus = 'create';
      },
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
