<style lang="scss">
  /*.section {*/
  /*  padding: 1rem 0 8px 0 !important;*/
  /*}*/

  .user-management {
    .img-wrapper {
      padding: 5px;
      border: solid 1px #bdbaba;
      border-radius: 3px;
      width: 190px;
      height: 270px;

      img {
        width: 100%;
        height: auto;
        object-fit: contain;
      }
    }

    $item-height: calc(50% - 0.3rem);
    $item-width: 25% ;
    $item-padding: calculateRem(20px);
    $item-extra-add-height: calculateRem(50px);
    .main-without-tab {
      overflow-x: hidden !important;
    }

    .item-wrapper {
      position: relative;
      height: fit-content !important;
      padding-left: $item-padding;
      display: inline-block;
      width: 100%;
      height: 100%;
      & > .item {
        z-index: 1;
        position: relative;
        height: 100%;
        width: 100%;
        display: inline-block;
        cursor: pointer;
        &:hover {
          box-shadow: 1px 2px 0 #c6c6c6;
        }
        &.active {
          .item-header {
            border-bottom-color: #009900;
          }
        }
        .item-header {
          background: #f3f3f3;
          border-bottom: solid 2px #c6c6c6;
          height: calculateRem(50px);
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 0 calculateRem(20px) 0 calculateRem(20px);
          .label {
            white-space: pre;
            font-size: calculateRem(15px);
            color: #666666;
            max-width: 100%;
            flex-grow: 1;
            text-overflow: ellipsis;
            overflow: hidden;
          }
          .action-list {
            white-space: pre;
            img {
              width: calculateRem(20px);
              margin-left: 0.5rem;
              &.disabled {
                filter: grayscale(1);
              }
              img:first-child {
                margin-left: 0;
              }
            }

          }
        }
        .item-body {
          padding: calculateRem(10px);
          .left-side {
            .action {
              button.btn {
                margin-bottom: calculateRem(10px);
                white-space: pre;
                font-size: calculateRem(11px);
                &.btn-success {
                  background-color: #49cf6f;
                  border-color: #49cf6f;
                  &:hover {
                    background-color: darken(#49cf6f, 8%);
                    border-color: darken(#49cf6f, 8%);
                  }
                }
                &.btn-info {
                  background-color: #1782d4;
                  &:hover {
                    background-color: darken(#1782d4, 8%);
                    border-color: darken(#1782d4, 8%);
                  }
                }
              }
            }
            .img {
              flex-grow: 1;
              width: 65px;
              height: 94px;
              display: flex;
              align-items: center;
              img {
                width: 90%;
                object-fit: contain;
              }
            }
          }
          .right-side {
            .text-top {
              color: #1782d4;
              font-weight: bold;
              margin-bottom: calculateRem(15px);
            }
            .content {
              & > div {
                display: flex;
                label {
                  white-space: pre;
                  overflow: visible;
                  text-overflow: ellipsis;
                  max-width: 100%;
                  color: #606266;
                  font-size: calculateRem(12px);
                  line-height: calculateRem(12px);
                  &:first-child {
                    width: 37%;
                    min-width: 37%;
                  }
                  &:last-child {
                    flex-grow: 1;
                  }
                  &.disabled {
                    color: #c0c0c0;
                  }
                }
              }

            }

            .caption {
              width: 37%;
            }
          }
        }
      }
      & > .item-extra-info {
        padding: calculateRem(18px);
        opacity: 0;
        transition: 0ms;
        border-radius: 0.3rem;
        position: absolute;
        top: 0;
        width: 80%;
        left: calculateRem(30px);
        background: wheat;
        z-index: 0;
        & > div {
          & > div {
            margin-bottom: calculateRem(4px);
            align-items: center;
            &:first-child {
              width: calculateRem(75px);
              margin-bottom: 0;
              font-size: 0.7rem;
              color: white;
              white-space: pre;
              overflow: hidden;
              text-overflow: ellipsis;
            }
            &:last-child {
              display: flex;
              align-items: center;
              flex-grow: 1;
              color: white;
              white-space: pre;
              overflow: hidden;
              text-overflow: ellipsis;
              img {
                width: calculateRem(12px);
              }
              span {
                font-size: 0.7rem;
                &.success {
                  color: #42b662;
                }
                &.pending {
                  color: #bbbbbb;
                }
                &.danger {
                  color: #e12c48;
                }
                margin-left: calculateRem(5px);
                &.without {
                  margin-left: calculateRem(18px);
                }
              }
              .chart-container {
                width: 100%;
                height: 100%;
              }
            }
          }
        }
      }
      &.slide-left {
        & > .item-extra-info {
          left: 0;
        }
        &:hover {
          & > .item-extra-info {
            left: calc(1.25rem - 100%);
          }

        }
      }
      &:hover {
        & > .item {
          z-index: 4;
        }
        & > .item-extra-info {
          top: -0.5rem;
          padding: 0.5rem;
          opacity: 0.9;
          transition: 10ms;
          left: 100%;
          z-index: 0;
        }

      }
    }
    .footer-pager {
      label {
        color: #666666;
      }
      .pagination {
        span {
          border: solid 1px #eeeeee;
          width: calculateRem(25px);
          height: calculateRem(25px);
          line-height: calculateRem(23px);
          text-align: center;
          font-size: calculateRem(12px);
          cursor: pointer;
          &.active {
            border: solid 1px #1782d4;
            background: #1782d4;
            color: white;
          }
          &:hover:not(.disabled) {
            border: solid 1px lighten(#1782d4, 20%);
            background: lighten(#1782d4, 20%);
            color: white;
          }
          &.disabled {
            cursor: not-allowed;
            border: solid 1px #eeeeee;
            background: #eeeeee;
            color: #939394;
          }
        }

      }
    }
  }

  .img-rotate {
    -ms-transform: rotate(-15deg); /* IE 9 */
    -webkit-transform: rotate(-15deg); /* Safari 3-8 */
    transform: rotate(-15deg);
  }
</style>
<template>
  <div class="user-management">
    <div class="breadcrumb-container">
      <b-row>
        <b-colxx xxs="12">
          <piaf-breadcrumb/>
        </b-colxx>
      </b-row>
    </div>

    <b-tabs v-show="!isLoading" nav-class="ml-2" :no-fade="true">

      <b-tab :title="$t('permission-management.member-table')" @click="tabStatus='user'">
        <b-row v-show="pageStatus==='table'" class="h-100 ">
          <b-col cols="12 d-flex flex-column" style="padding-right: 15px; padding-left: 15px;">
            <b-row class="pt-2">
              <b-col cols="7">
                <b-row>
                  <b-col>
                    <b-form-group :label="$t('permission-management.username')">
                      <b-form-input v-model="filter.userName"/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('permission-management.status')">
                      <b-form-select v-model="filter.status" :options="statusSelectData" plain/>
                    </b-form-group>
                  </b-col>
                  <b-col>
                    <b-form-group :label="$t('permission-management.gender')">
                      <b-form-select v-model="filter.gender" :options="genderFilterOptions" plain/>
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
              <b-col cols="5" class="d-flex justify-content-end align-items-center">
                <b-button size="sm" class="ml-2" variant="info default" @click="onSearchButton()">
                  <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="info default" @click="onResetButton()">
                  <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('user_export')"
                          @click="onExportButton()">
                  <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                </b-button>
                <b-button size="sm" class="ml-2" variant="outline-info default" :disabled="checkPermItem('user_print')"
                          @click="onPrintUserButton()">
                  <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                </b-button>
                <b-button size="sm" class="ml-2" @click="onCreatePage()" :disabled="checkPermItem('user_create')"
                          variant="success default">
                  <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                </b-button>
              </b-col>
            </b-row>
            <b-row class="flex-grow-1">
              <b-col cols="12">
                <div class="table-wrapper table-responsive">
                  <vuetable
                    ref="vuetable"
                    :api-url="vuetableItems.apiUrl"
                    :fields="vuetableItems.fields"
                    :http-fetch="userTableHttpFetch"
                    :per-page="vuetableItems.perPage"
                    track-by="userId"
                    pagination-path="pagination"
                    class="table-striped"
                    @vuetable:checkbox-toggled="onCheckStatusChange"
                    @vuetable:pagination-data="onUserTablePaginationData"
                  >
                    <template slot="userNumber" slot-scope="props">
                      <span class="cursor-p text-primary" @click="onAction('show', props.rowData, props.rowIndex)">{{ props.rowData.userNumber }}</span>
                    </template>
                    <template slot="actions" slot-scope="props">
                      <div>

                        <b-button
                          size="sm"
                          variant="primary default btn-square"
                          :disabled="checkPermItem('user_modify') || props.rowData.status==='1000000304' || props.rowData.status==='1000000303'"
                          @click="onAction('modify', props.rowData, props.rowIndex)">
                          <i class="icofont-edit"/>
                        </b-button>

                        <!--                        <b-button-->
                        <!--                          v-if="props.rowData.status!=='1000000302'"-->
                        <!--                          size="sm"-->
                        <!--                          variant="primary default btn-square"-->
                        <!--                          disabled>-->
                        <!--                          <i class="icofont-edit"/>-->
                        <!--                        </b-button>-->

                        <b-button
                          v-if="props.rowData.status==='1000000302'"
                          size="sm"
                          variant="success default btn-square"
                          @click="onAction('activate', props.rowData, props.rowIndex)"
                          :disabled="checkPermItem('user_update_status')">
                          <i class="icofont-check-circled"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status==='1000000301'"
                          size="sm"
                          :disabled="checkPermItem('user_update_status')"
                          variant="warning default btn-square"
                          @click="onAction('inactivate', props.rowData, props.rowIndex)">
                          <i class="icofont-ban"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!=='1000000302' && props.rowData.status!=='1000000301'"
                          size="sm"
                          variant="success default btn-square"
                          disabled>
                          <i class="icofont-check-circled"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status==='1000000302'"
                          size="sm"
                          variant="danger default btn-square"
                          :disabled="checkPermItem('user_update_status')"
                          @click="onAction('blocked', props.rowData, props.rowIndex)">
                          <i class="icofont-minus-circle"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status==='1000000303'"
                          size="sm"
                          variant="success default btn-square"
                          :disabled="checkPermItem('user_update_status')"
                          @click="onAction('unblock', props.rowData, props.rowIndex)">
                          <i class="icofont-power"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!=='1000000302' && props.rowData.status!=='1000000303'"
                          size="sm"
                          variant="danger default btn-square"
                          disabled>
                          <i class="icofont-minus-circle"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status==='1000000304'"
                          size="sm"
                          variant="purple default btn-square"
                          :disabled="checkPermItem('user_modify')"
                          @click="onAction('reset-password', props.rowData, props.rowIndex)">
                          <i class="icofont-ui-password"/>
                        </b-button>

                        <b-button
                          v-if="props.rowData.status!=='1000000304'"
                          size="sm"
                          variant="purple default btn-square"
                          disabled>
                          <i class="icofont-ui-password"/>
                        </b-button>

                      </div>
                    </template>

                  </vuetable>
                </div>
                <div class="pagination-wrapper">
                  <vuetable-pagination-bootstrap
                    ref="pagination"
                    @vuetable-pagination:change-page="onUserTableChangePage"
                    :initial-per-page="vuetableItems.perPage"
                    @onUpdatePerPage="vuetableItems.perPage = Number($event)"
                  />
                </div>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <b-row v-show="pageStatus==='create'" class="h-100 form-section">
          <b-col cols="10">
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userName"
                                :state="!$v.profileForm.userName.$dirty ? null : !$v.profileForm.userName.$invalid"
                                :placeholder="$t('permission-management.please-enter-user-name')"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.userName.required) ?
                    $t('permission-management.user.username-field-is-mandatory') :
                    (!$v.profileForm.userName.maxLength)?$t('permission-management.user.account-should-less-50-letter'):"&nbsp;"
                    }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input :disabled="modifyPage" type="text" v-model="profileForm.userNumber"
                                :state="!$v.profileForm.userNumber.$dirty ? null : !$v.profileForm.userNumber.$invalid"
                                :placeholder="$t('permission-management.please-enter-user-id')"/>
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
                  <b-form-select :disabled="profileForm.status==='1000000301'" v-model="profileForm.gender"
                                 :options="genderOptions" plain
                                 :state="!$v.profileForm.gender.$dirty ? null : !$v.profileForm.gender.$invalid"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.gender.required) ?
                    $t('permission-management.user.gender-field-is-mandatory') : "&nbsp;" }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.license-number')}}</template>
                  <b-form-input type="text" v-model="profileForm.identityCard"
                                :placeholder="$t('permission-management.please-enter-license-number')"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.affiliated-institution')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :disabled="profileForm.status==='1000000301'" v-model="profileForm.orgId"
                                 :options="orgNameSelectData" plain
                                 :state="!$v.profileForm.orgId.$dirty ? null : !$v.profileForm.orgId.$invalid"/>
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
                                :placeholder="$t('permission-management.please-enter-post')"/>
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
                  <b-form-input type="email"
                                v-model="profileForm.email"
                                :state="!$v.profileForm.email.$dirty ? null : !$v.profileForm.email.$invalid"
                                :placeholder="$t('permission-management.please-enter-email')"/>
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
                                :state="!$v.profileForm.mobile.$dirty ? null : !$v.profileForm.mobile.$invalid"
                                :placeholder="'000-0000-0000'"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.address')}}</template>
                  <b-form-input type="text" v-model="profileForm.address"
                                :placeholder="$t('permission-management.please-enter-address')"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text"
                                :disabled="modifyPage"
                                v-model="profileForm.userAccount"
                                :state="!$v.profileForm.userAccount.$dirty ? null : !$v.profileForm.userAccount.$invalid"
                                :placeholder="$t('permission-management.please-enter-user-account')"/>
                  <div class="invalid-feedback d-block">
                    {{ (submitted && !$v.profileForm.userAccount.required) ?
                    $t('permission-management.user.account-field-is-mandatory') : " " }}
                  </div>
                </b-form-group>
              </b-col>
              <b-col cols="4">
                <b-form-group style="max-width: unset">
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
                                    :state="!$v.profileForm.passwordValue.$dirty ? null : !$v.profileForm.passwordValue.$invalid"
                                    :placeholder="$t('permission-management.please-enter-password')"/>
                    </div>
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group :label="$t('permission-management.note')">
                  <b-form-textarea type="text" v-model="profileForm.note" style="height: 50px; !important"
                                   :placeholder="$t('permission-management.please-enter-note')"/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="2" class="d-flex align-items-center flex-column">
            <div class="mb-4 img-wrapper position-relative">
              <div class=" p-1">
                <img :src="profileForm.avatar" onerror="src=''" class="card-img-top"/>
              </div>
              <div v-if="getLocale()==='zh'" class="position-absolute" style="bottom: -18%;left: -50%">
                <img v-if="modifyPage === false" src="../../../assets/img/no_active_stamp.png">
                <img v-if="profileForm.status==='1000000301'" src="../../../assets/img/active_stamp.png">
                <img v-else-if="profileForm.status==='1000000302'" src="../../../assets/img/no_active_stamp.png">
                <img v-else-if="profileForm.status==='1000000303'" src="../../../assets/img/block.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000304'" src="../../../assets/img/pending.png"
                     class="img-rotate">
              </div>
              <div v-if="getLocale()==='en'" class="position-absolute" style="bottom: -18%;left: -50%">
                <img v-if="modifyPage === false" src="../../../assets/img/no_active_stamp_en.png"
                     class="img-rotate">
                <img v-if="profileForm.status==='1000000301'" src="../../../assets/img/active_stamp_en.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000302'" src="../../../assets/img/no_active_stamp_en.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000303'" src="../../../assets/img/block_en.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000304'" src="../../../assets/img/pending_en.png"
                     class="img-rotate">
              </div>
              <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
            </div>
            <b-button @click="$refs.profileFile.click()" class="mb-1" variant="info skyblue default" size="sm">{{
              $t('permission-management.upload-image')}}
            </b-button>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <div>
              <b-button @click="onSaveUserPage()" variant="success default" size="sm"><i
                class="icofont-save"/> {{
                $t('permission-management.save') }}
              </b-button>
              <b-button :disabled="checkPermItem('user_update_status')"
                        v-if="profileForm.status==='1000000301' && modifyPage===true" class="mr-1"
                        @click="onAction('inactivate', profileForm)"
                        variant="warning default" size="sm"><i class="icofont-ban"/> {{
                $t('permission-management.action-make-inactive') }}
              </b-button>
              <b-button :disabled="checkPermItem('user_update_status')"
                        v-if="profileForm.status==='1000000302' && modifyPage===true" class="mr-1"
                        @click="onAction('activate', profileForm)"
                        variant="success default" size="sm"><i class="icofont-check-circled"/> {{
                $t('permission-management.active') }}
              </b-button>
              <b-button :disabled="checkPermItem('user_update_status')"
                        v-if="profileForm.status==='1000000302' && modifyPage===true" class="mr-1"
                        @click="onAction('blocked', profileForm)"
                        variant="danger default" size="sm"><i class="icofont-minus-circle"/> {{
                $t('permission-management.action-block') }}
              </b-button>
              <b-button :disabled="checkPermItem('user_update_status')"
                        v-if="profileForm.status==='1000000303' && modifyPage===true" class="mr-1"
                        @click="onAction('unblock', profileForm)"
                        variant="success default" size="sm"><i class="icofont-power"/> {{
                $t('permission-management.action-unblock') }}
              </b-button>
              <b-button :disabled="checkPermItem('user_modify')"
                        v-if="profileForm.status==='1000000304' && modifyPage===true" class="mr-1"
                        @click="onAction('reset-password', profileForm)"
                        variant="purple default" size="sm"><i class="icofont-ui-password"/> {{
                $t('permission-management.pending') }}
              </b-button>
              <b-button @click="onTableListPage()" variant="danger default" size="sm"><i
                class="icofont-long-arrow-left"/> {{
                $t('permission-management.return') }}
              </b-button>
            </div>
          </b-col>
        </b-row>
        <b-row v-show="pageStatus==='show'" class="h-100 form-section">
          <b-col cols="10">
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-username')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input type="text" v-model="profileForm.userName"
                                :placeholder="$t('permission-management.please-enter-user-name')"/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.th-user-id')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input :disabled="true" type="text" v-model="profileForm.userNumber"
                                :placeholder="$t('permission-management.please-enter-user-id')"/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.gender')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-select :disabled="profileForm.status==='1000000301'" v-model="profileForm.gender"
                                 :options="genderOptions" plain
                  />
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.license-number')}}</template>
                  <b-form-input type="text" v-model="profileForm.identityCard"
                                :placeholder="$t('permission-management.please-enter-license-number')"/>

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
                                :placeholder="$t('permission-management.please-enter-post')"/>
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
                                :placeholder="$t('permission-management.please-enter-email')"/>
                </b-form-group>
              </b-col>
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.phone')}}</template>
                  <b-form-input type="text" v-model="profileForm.mobile"
                                :placeholder="$t('permission-management.please-enter-phone')"/>
                </b-form-group>
              </b-col>
              <b-col cols="6">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.address')}}</template>
                  <b-form-input type="text" v-model="profileForm.address"
                                :placeholder="$t('permission-management.please-enter-address')"/>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row class="mb-2">
              <b-col cols="3">
                <b-form-group>
                  <template slot="label">{{$t('permission-management.user-account')}}&nbsp;<span
                    class="text-danger">*</span></template>
                  <b-form-input disabled type="text" v-model="profileForm.userAccount"
                                :placeholder="$t('permission-management.please-enter-user-account')"/>
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
                                    :state="!$v.profileForm.passwordValue.$dirty ? null : !$v.profileForm.passwordValue.$invalid"
                                    :placeholder="$t('permission-management.please-enter-password')"/>
                    </div>
                  </div>
                </b-form-group>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="6">
                <b-form-group :label="$t('permission-management.note')">
                  <b-form-textarea type="text" v-model="profileForm.note" style="height: 50px; !important"
                                   :placeholder="$t('permission-management.please-enter-note')"/>
                </b-form-group>
              </b-col>
            </b-row>

          </b-col>
          <b-col cols="2" class="text-right">
            <div class="mb-4 img-wrapper position-relative">
              <div class=" p-1">
                <img :src="profileForm.avatar" onerror="src=''" class="card-img-top"/>
              </div>
              <div v-if="getLocale()==='zh'" class="position-absolute" style="bottom: -18%;left: -50%">
                <img v-if="profileForm.status==='1000000301'" src="../../../assets/img/active_stamp.png">
                <img v-else-if="profileForm.status==='1000000302'" src="../../../assets/img/no_active_stamp.png">
                <img v-else-if="profileForm.status==='1000000303'" src="../../../assets/img/block.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000304'" src="../../../assets/img/pending.png"
                     class="img-rotate">
              </div>
              <div v-if="getLocale()==='en'" class="position-absolute" style="bottom: -18%;left: -50%">
                <img v-if="profileForm.status==='1000000301'" src="../../../assets/img/active_stamp_en.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000302'" src="../../../assets/img/no_active_stamp_en.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000303'" src="../../../assets/img/block_en.png"
                     class="img-rotate">
                <img v-else-if="profileForm.status==='1000000304'" src="../../../assets/img/pending_en.png"
                     class="img-rotate">
              </div>
              <input type="file" ref="profileFile" @change="onFileChange" style="display: none"/>
            </div>
          </b-col>
          <b-col cols="12" class="d-flex justify-content-end align-self-end">
            <b-button :disabled="checkPermItem('user_update_status')" v-if="profileForm.status==='1000000301'"
                      class="mr-1" @click="onAction('inactivate', profileForm)"
                      variant="warning default" size="sm"><i class="icofont-ban"/> {{
              $t('permission-management.action-make-inactive') }}
            </b-button>
            <b-button :disabled="checkPermItem('user_update_status')" v-if="profileForm.status==='1000000302'"
                      class="mr-1" @click="onAction('activate', profileForm)"
                      variant="success default" size="sm"><i class="icofont-check-circled"/> {{
              $t('permission-management.active') }}
            </b-button>
            <b-button @click="onTableListPage()" variant="danger default" size="sm"><i
              class="icofont-long-arrow-left"/> {{
              $t('permission-management.return') }}
            </b-button>
          </b-col>
        </b-row>
      </b-tab>

      <b-tab :title="$t('permission-management.user-group')" @click="tabStatus='group'">
        <b-row class="h-100 ">
          <b-col cols="8" class="d-flex flex-column">
            <div class="section d-flex flex-column h-100">
              <b-row class="m-0">
                <b-col cols="4" class="pr-3">
                  <b-row>
                    <b-col>
                      <b-form-group class="search-form-group">
                        <template slot="label">{{$t('permission-management.user.user-group-name')}}</template>
                        <b-form-input v-model="groupFilter.groupName"/>
                      </b-form-group>
                    </b-col>
                    <b-col>
                      <b-form-group class="search-form-group">
                        <template slot="label">{{$t('permission-management.user.user-group-member')}}</template>
                        <b-form-input v-model="groupFilter.userName"/>
                      </b-form-group>
                    </b-col>
                  </b-row>
                </b-col>
                <b-col cols="8" class="d-flex justify-content-end align-items-center">
                  <div>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onUserGroupSearchButton()">
                      <i class="icofont-search-1"/>&nbsp;{{ $t('permission-management.search') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="info default" @click="onUserGroupResetButton()">
                      <i class="icofont-ui-reply"/>&nbsp;{{$t('permission-management.reset') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default"
                              :disabled="checkPermItem('user_group_export')" @click="onExportButton()">
                      <i class="icofont-share-alt"/>&nbsp;{{ $t('permission-management.export') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" variant="outline-info default"
                              :disabled="checkPermItem('user_group_print')" @click="onPrintGroupButton()">
                      <i class="icofont-printer"/>&nbsp;{{ $t('permission-management.print') }}
                    </b-button>
                    <b-button size="sm" class="ml-2" @click="onUserGroupCreateButton()"
                              :disabled="checkPermItem('user_group_create')" variant="success default">
                      <i class="icofont-plus"/>&nbsp;{{$t('permission-management.new') }}
                    </b-button>
                  </div>
                </b-col>
              </b-row>

              <b-row class="flex-grow-1 m-0">
                <b-col cols="12">
                  <div class="table-wrapper table-responsive">
                    <vuetable
                      ref="userGroupTable"
                      :api-url="userGroupTableItems.apiUrl"
                      :fields="userGroupTableItems.fields"
                      :http-fetch="userGroupTableHttpFetch"
                      pagination-path="userGroupPagination"
                      track-by="userGroupId"
                      class="table-hover"
                      @vuetable:checkbox-toggled="onCheckStatusChangeGroup"
                      @vuetable:pagination-data="onUserGroupTablePaginationData"
                    >
                      <template slot="userGroupNumber" slot-scope="props">
                        <span class="cursor-p text-primary" @click="onUserGroupTableRowClick(props.rowData)">{{ props.rowData.groupNumber }}</span>
                      </template>
                      <template slot="operating" slot-scope="props">
                        <b-button variant="danger default btn-square" class="m-0"
                                  :disabled="checkPermItem('user_group_delete')"
                                  @click="onAction('group-remove', props.rowData, props.rowIndex)"><i
                          class="icofont-bin"/></b-button>
                      </template>
                    </vuetable>
                  </div>
                  <div class="pagination-wrapper">
                    <vuetable-pagination-bootstrap
                      ref="userGroupPagination"
                      @vuetable-pagination:change-page="onUserGroupTableChangePage"
                      :initial-per-page="userGroupTableItems.perPage"
                      @onUpdatePerPage="userGroupTableItems.perPage = Number($event)"
                    />
                    <b-modal centered ref="modal-prompt-group" :title="$t('permission-management.prompt')">
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
            </div>
          </b-col>
          <b-col cols="4" class="pl-0 " v-if="selectedUserGroupItem">
            <div class="section d-flex flex-column h-100 px-3">
              <div v-if="groupForm.status==='create'">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-number')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <label>
                    {{groupForm.groupNumber}}
                  </label>
                </b-form-group>
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-if="groupForm.status==='create'"
                                v-model="groupForm.groupName"
                                :state="!$v.groupForm.groupName.$invalid"/>
                </b-form-group>
              </div>
              <div v-if="groupForm.status!=='create'">
                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-number')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <label>
                    {{selectedUserGroupItem.groupNumber}}
                  </label>

                </b-form-group>

                <b-form-group>
                  <template slot="label">
                    {{$t('permission-management.user.group-name')}}&nbsp;
                    <span class="text-danger">*</span>
                  </template>
                  <b-form-input v-model="selectedUserGroupItem.groupName"
                                />
                </b-form-group>
              </div>
              <div>
                <label class="font-weight-bold">{{$t('permission-management.user.group-member')}}<span
                  class="text-danger">*</span></label>
              </div>
              <div class="text-left">
                <b-form-group>
                  <b-form-checkbox v-model="isSelectedAllUsersForDataGroup">
                    {{$t('permission-management.permission-control.select-all')}}
                  </b-form-checkbox>
                </b-form-group>
              </div>
              <div class="flex-grow-1 overflow-auto" style="height: 0;">

                <div>
                  <v-tree ref='orgUserTree' :data='orgUserTreeData' :multiple="true" :halfcheck='true'/>
                </div>
              </div>
              <div class="d-flex align-items-end justify-content-end pt-3" v-if="groupForm.status==='create'">
                <div>
                  <b-button @click="onClickCreateUserGroup" variant="info default" size="sm"><i
                    class="icofont-save"/> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="selectedUserGroupItem = false"
                            variant="danger default" size="sm"><i
                    class="icofont-long-arrow-left"/> {{$t('system-setting.cancel')}}
                  </b-button>
                </div>
              </div>
              <div class="d-flex align-items-end justify-content-end pt-3" v-if="groupForm.status!=='create'">
                <div>
                  <b-button @click="onClickModifyUserGroup" variant="info default"
                            :disabled="checkPermItem('user_group_modify')" size="sm"><i
                    class="icofont-save"/> {{$t('permission-management.permission-control.save')}}
                  </b-button>
                  <b-button @click="onClickDeleteUserGroup" :disabled="checkPermItem('user_group_delete')"
                            variant="danger default" size="sm"><i
                    class="icofont-bin"/> {{$t('permission-management.delete')}}
                  </b-button>

                </div>
              </div>
            </div>
          </b-col>
        </b-row>
      </b-tab>
    </b-tabs>
    <div v-show="isLoading" class="loading"></div>
    <b-modal centered ref="modal-prompt" :title="$t('permission-management.prompt')">
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
    <b-modal centered ref="modal-active" :title="$t('permission-management.prompt')">
      {{promptTemp.action==='unblock'?$t('permission-management.user.unblock-prompt'):$t('permission-management.user.active-prompt')}}
      <template slot="modal-footer">
        <b-button variant="primary" @click="fnChangeItemStatus()" class="mr-1">
          {{$t('permission-management.modal-ok')}}
        </b-button>
        <b-button variant="danger" @click="fnHideModal('modal-active')">
          {{$t('permission-management.modal-cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="modal-reset" ref="modal-reset">
      <template slot="modal-header">
        <h2 style="font-size: 1.7rem; font-weight: bold;" class="modal-title">
          {{$t('password-reset.password-change')}}</h2>
        <button type="button" aria-label="Close" @click="fnHideModal('modal-reset')" class="close">×</button>
      </template>
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <div style="width: 100%">
            <b-form-group class="mw-100">
              <template slot="label">{{$t('password-reset.new-password')}}<span
                class="text-danger">*</span>
              </template>
              <b-form-input type="password" v-model="passwordForm.password"
                            :state="!$v.passwordForm.password.$dirty ? null : !$v.passwordForm.password.$invalid"
                            class="mw-100"/>
            </b-form-group>
            <b-form-group>
              <template slot="label">{{$t('password-reset.confirm-password')}}<span
                class="text-danger">*</span>
              </template>
              <b-form-input type="password" v-model="passwordForm.confirmPassword"
                            :state="!$v.passwordForm.confirmPassword.$dirty ? null : !$v.passwordForm.confirmPassword.$invalid"
                            class="mw-100"/>
            </b-form-group>
          </div>
        </b-col>
      </b-row>
      <template slot="modal-footer">
        <b-button variant="primary default" @click="resetPassword(promptTemp)" class="mr-1">
          {{$t('password-reset.confirm')}}
        </b-button>
        <b-button variant="light default" @click="fnHideModal('modal-reset')">{{$t('system-setting.cancel')}}
        </b-button>
      </template>
    </b-modal>

    <b-modal centered id="model-export" ref="model-export">
      <b-row>
        <b-col cols="12" class="d-flex justify-content-center">
          <h3 class="text-center font-weight-bold" style="margin-bottom: 1rem;">{{ $t('permission-management.export')
            }}</h3>
        </b-col>
      </b-row>
      <b-row style="height : 100px;">
        <b-col style="margin-top: 1rem; margin-left: 6rem; margin-right: 6rem;">
          <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid" :searchable="false"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </b-col>
      </b-row>
      <div slot="modal-footer">
        <b-button size="sm" variant="orange default" @click="onExportButtonModel()">
          <i class="icofont-gift"/>
          {{ $t('permission-management.export') }}
        </b-button>
        <b-button size="sm" variant="light default" @click="fnHideModal('model-export')">
          <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
        </b-button>
      </div>
    </b-modal>
    <Modal
      ref="exportModal"
      v-if="isModalVisible"
      :link="link" :params="params" :name="name"
      @close="closeModal"
    />

  </div>

</template>
<script>

  import {apiBaseUrl} from "../../../constants/config";
  import Vuetable from '../../../components/Vuetable2/Vuetable'
  import VuetablePaginationBootstrap from "../../../components/Common/VuetablePaginationBootstrap";
  import {checkPermissionItem, getDirection, getLocale} from "../../../utils";
  import {
    getApiManager,
    isPhoneValid,
    isAccountValid,
    isGroupNumberValid,
    printFileFromServer,
    getApiManagerError
  } from '../../../api';
  import {responseMessages} from '../../../constants/response-messages';
  import {validationMixin} from 'vuelidate';
  import VTree from 'vue-tree-halower';
  import 'vue-tree-halower/dist/halower-tree.min.css' // you can customize the style of the tree
  import vSelect from 'vue-select'
  import 'vue-select/dist/vue-select.css'
  import Modal from '../../../components/Modal/modal'

  const {required, email, minLength, maxLength, alphaNum, sameAs} = require('vuelidate/lib/validators');

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
      'v-select': vSelect,
      'v-tree': VTree,
      Modal
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
      passwordForm: {
        password: {
          required, minLength: minLength(6), maxLength: maxLength(20),
          isAccountValid
        },
        confirmPassword: {
          required, sameAs: sameAs('password'), maxLength: maxLength(20),
        },
      },
      profileForm: {
        userName: {
          required, maxLength: maxLength(20)
        },
        userNumber: {
          required, alphaNum, maxLength: maxLength(20)
        },
        gender: {
          required, maxLength: maxLength(20),
        },
        identityCard: {
          maxLength: maxLength(20),
        },
        orgId: {
          required, maxLength: maxLength(20),
        },
        email: {
          email, maxLength: maxLength(20),
        },
        userAccount: {
          isAccountValid,
          required, minLength: minLength(6), maxLength: maxLength(16),
        },
        passwordValue: {
          isAccountValid,
          required, minLength: minLength(6), maxLength: maxLength(20),
        },
        mobile: {
          isPhoneValid,
        }
      },

      groupForm: {
        groupName: {
          required, maxLength: maxLength(20),
        },
        groupNumber: {
          required, maxLength: maxLength(20),
          isGroupNumberValid
        }
      }
    },
    mounted() {

      this.$refs.vuetable.$parent.transform = this.transform.bind(this);
      this.$refs.userGroupTable.$parent.transform = this.fnTransformUserGroupTable.bind(this);
      getApiManagerError().post(`${apiBaseUrl}/permission-management/organization-management/organization/get-all`, {
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
      getApiManagerError().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {
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
        isLoading: false,
        submitted: false,
        tabStatus: 'user',
        link: '',
        params: {},
        name: '',
        fileSelection: [],
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
        renderedCheckList: [],
        renderedCheckListGroup: [],
        isModalVisible: false,
        tableData: [],
        pageStatus: 'table',
        modifyPage: false,
        defaultOrgId: '',
        filter: {
          userName: '',
          status: null,
          orgId: null,
          gender: null
        },

        passwordForm: {
          password: null,
          confirmPassword: null
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
          {value: '1000000001', text: this.$t('permission-management.male')},
          {value: '1000000002', text: this.$t('permission-management.female')},
        ],
        genderFilterOptions: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000001', text: this.$t('permission-management.male')},
          {value: '1000000002', text: this.$t('permission-management.female')},
        ],
        statusSelectData: [
          {value: null, text: this.$t('permission-management.all')},
          {value: '1000000301', text: this.$t('permission-management.active')},
          {value: '1000000302', text: this.$t('permission-management.inactive')},
          {value: '1000000304', text: this.$t('permission-management.pending')},
          {value: '1000000303', text: this.$t('permission-management.blocked')},
        ],
        orgNameSelectData: [],
        educationOptions: [
          {value: '1000000101', text: this.$t('permission-management.belowcollege')},
          {value: '1000000102', text: this.$t('permission-management.student')},
          {value: '1000000103', text: this.$t('permission-management.master_student')},
          {value: '1000000104', text: this.$t('permission-management.doctor_student')},
          {value: '1000000105', text: this.$t('permission-management.other')},
        ],
        degreeOptions: [
          {value: '1000000201', text: this.$t('permission-management.belowcollege')},
          {value: '1000000202', text: this.$t('permission-management.bachelor')},
          {value: '1000000203', text: this.$t('permission-management.master')},
          {value: '1000000204', text: this.$t('permission-management.doctor')},
          {value: '1000000205', text: this.$t('permission-management.other')},
        ],

        profileForm: {
          status: '1000000102',
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
          userAccount: '',
          passwordType: 'default',
          passwordValue: '',
          note: '',
          portrait: null
        },
        vuetableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/user/get-by-filter-and-page`,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '6%'
            },
            {
              name: '__slot:userNumber',
              title: this.$t('permission-management.th-user-id'),
              sortField: 'userNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '12%'
            },
            {
              name: 'userName',
              title: this.$t('permission-management.th-username'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '12%'
            },
            {
              name: 'gender',
              title: this.$t('permission-management.gender'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000001": `<span>${this.$t('permission-management.male')}</span>`,
                  "1000000002": `<span>${this.$t('permission-management.female')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              },
              width: '11%'
            },
            {
              name: 'status',
              title: this.$t('permission-management.th-status'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              callback: (value) => {
                const dictionary = {
                  "1000000301": `<span class="text-success">${this.$t('permission-management.active')}</span>`,
                  "1000000302": `<span class="text-muted">${this.$t('permission-management.inactive')}</span>`,
                  "1000000303": `<span class="text-danger">${this.$t('permission-management.blocked')}</span>`,
                  "1000000304": `<span class="text-warning">${this.$t('permission-management.pending')}</span>`,
                };
                if (!dictionary.hasOwnProperty(value)) return '';
                return dictionary[value];
              },
              width: '11%',
            },
            {
              name: 'orgName',
              title: this.$t('permission-management.th-affiliated-institution'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            },
            {
              name: 'userAccount',
              title: this.$t('permission-management.th-account'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '13%'
            },
            {
              name: '__slot:actions',
              title: this.$t('permission-management.th-action'),
              titleClass: 'text-center',
              dataClass: 'text-center btn-actions',
              width: '250px'
            },

          ],
          perPage: 10,
        },
        //second tab content
        selectedUserGroupItem: null,
        orgUserTreeDataTmp: [],
        groupForm: {
          groupName: '',
          groupNumber: '',
          status: 'create'
        },
        groupFilter: {
          groupName: '',
          userName:''
        },
        userGroupTableItems: {
          apiUrl: `${apiBaseUrl}/permission-management/user-management/user-group/get-by-filter-and-page`,
          perPage: 10,
          fields: [
            {
              name: '__checkbox',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width: '60px'
            },
            {
              name: '__sequence',
              title: this.$t('permission-management.th-no'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width : '6%'
            },
            {
              name: '__slot:userGroupNumber',
              title: this.$t('permission-management.user.user-group-number'),
              sortField: 'groupNumber',
              titleClass: 'text-center',
              dataClass: 'text-center',
              width : '15%'
            },
            {
              name: 'groupName',
              title: this.$t('permission-management.user.user-group-name'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width : '20%'
            },
            {
              name: 'groupMember',
              title: this.$t('permission-management.user.user-group-member'),
              titleClass: 'text-center',
              dataClass: 'text-center',
              width : '30%',
              callback: (value) => {
                if(value === null) return '';
                if(value.isLong === false) return value.groupMember;
                else{
                  return this.hoverContent(value);
                }
              },

            },
            {
              name: '__slot:operating',
              title: this.$t('permission-management.user.operating'),
              titleClass: 'text-center',
              dataClass: 'text-center',
            }
          ],
        },

        isSelectedAllUsersForDataGroup: false,
        isSelectedId : null,
      }
    },
    watch: {
      'vuetableItems.perPage': function (newVal) {
        this.$refs.vuetable.refresh();
        this.changeCheckAllStatus();
      },
      'userGroupTableItems.perPage': function (newVal) {
        this.$refs.userGroupTable.refresh();
        this.changeCheckAllStatusGroup();
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

        this.changeOrgTree(this.treeData.children, 1);
        this.orgNameSelectData.unshift({
          text: this.treeData.orgName,
          value: this.treeData.orgId
        });
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

        //this.orgNameSelectData = selectOptions;

        this.filter.orgId = null;
        this.defaultOrgId = this.treeData.orgId;
        this.fnRefreshOrgUserTreeData();
      },
      userData(newVal) {
        this.fnRefreshOrgUserTreeData();
      },
      selectedUserGroupItem(newVal) {
        if (newVal) {
          if (newVal.users.length === this.userData.length) {
            this.isSelectedAllUsersForDataGroup = true;
          } else {
            this.isSelectedAllUsersForDataGroup = false;
          }
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
          this.fnRefreshOrgUserTreeData();
        }
      }
    },
    methods: {
      getAllUser(){
        getApiManagerError().post(`${apiBaseUrl}/permission-management/user-management/user/get-all`, {
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
      hoverContent(value) {
        let content = '<div class="item-wrapper slide-right">\n' +
          '      <span class="item d-flex flex-column">\n' + value.label +
          '      </span>\n' +
          '      <div class="item-extra-info flex-column d-flex">\n' + value.groupMember +
          '      </div>\n' +
          '    </div>';
        return content;
      },
      changeOrgTree(treeData, index) {


        if (!treeData || treeData.length === 0) {
          return;
        }

        let tmp = treeData;

        for (let i = 0; i < tmp.length; i++) {
          this.changeOrgTree(tmp[i].children, index + 1);

          this.orgNameSelectData.unshift({
            value: tmp[i].orgId,
            html: `${this.generatSpace(index)}${tmp[i].orgName}`
          });
        }
      },
      generatSpace(count) {
        let string = '';
        while (count--) {
          string += '&nbsp;&nbsp;&nbsp;&nbsp;';
        }
        return string;
      },
      getLocale() {
        return getLocale();
      },
      selectAll(value) {
        this.$refs.vuetable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.vuetable.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNone() {
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.vuetable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatus() {
        let selectList = this.$refs.vuetable.selectedTo;
        let renderedList = this.renderedCheckList;
        if (selectList.length >= renderedList.length) {
          let isEqual = false;
          for (let i = 0; i < renderedList.length; i++) {
            isEqual = false;
            for (let j = 0; j < selectList.length; j++) {
              if (renderedList[i] === selectList[j]) {
                j = selectList.length;
                isEqual = true
              }
            }
            if (isEqual === false) {
              this.selectNone();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAll(true);
            }
          }
        } else {
          this.selectNone();
        }

      },
      selectAllGroup(value) {
        this.$refs.userGroupTable.toggleAllCheckboxes('__checkbox', {target: {checked: value}});
        this.$refs.userGroupTable.isCheckAllStatus = value;
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.userGroupTable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = value;
      },
      selectNoneGroup() {
        let checkBoxId = "vuetable-check-header-2-" + this.$refs.userGroupTable.uuid;
        let checkAllButton = document.getElementById(checkBoxId);
        checkAllButton.checked = false;
      },
      changeCheckAllStatusGroup() {
        let selectList = this.$refs.userGroupTable.selectedTo;
        let renderedList = this.renderedCheckListGroup;
        if (selectList.length >= renderedList.length) {
          let isEqual = false;
          for (let i = 0; i < renderedList.length; i++) {
            isEqual = false;
            for (let j = 0; j < selectList.length; j++) {
              if (renderedList[i] === selectList[j]) {
                j = selectList.length;
                isEqual = true
              }
            }
            if (isEqual === false) {
              this.selectNoneGroup();
              break;
            }
            if (i === renderedList.length - 1) {
              this.selectAllGroup(true);
            }
          }
        } else {
          this.selectNoneGroup();
        }

      },
      onCheckStatusChange(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatus();
        } else {
          this.selectNone();
        }
      },
      onCheckStatusChangeGroup(isChecked) {
        if (isChecked) {
          this.changeCheckAllStatusGroup();
        } else {
          this.selectNoneGroup();
        }
      },
      closeModal() {
        this.isModalVisible = false;
      },
      checkPermItem(value) {
        return checkPermissionItem(value);
      },
      onExportButton() {
        // this.fileSelection = [];
        // this.$refs['model-export'].show();
        if (this.tabStatus === 'user') {
          this.onExportUser();
        }
        if (this.tabStatus === 'group') {
          this.onExportGroup();
        }
        this.isModalVisible = true;
      },
      onExportButtonModel() {
        if (this.tabStatus === 'user') {
          this.onExportUser();
        }
        if (this.tabStatus === 'group') {
          this.onExportGroup();
        }
      },
      onExportUser() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        this.link = `permission-management/user-management/user`;
        this.name = 'User';
      },
      onPrintUserButton() {
        let checkedAll = this.$refs.vuetable.checkedAllStatus;
        let checkedIds = this.$refs.vuetable.selectedTo;
        let httpOption = this.$refs.vuetable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.filter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/user-management/user`;
        printFileFromServer(link, params);
      },

      onExportGroup() {
        let checkedAll = this.$refs.userGroupTable.checkedAllStatus;
        let checkedIds = this.$refs.userGroupTable.selectedTo;
        let httpOption = this.$refs.userGroupTable.httpOptions;
        this.params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.groupFilter,
          'idList': checkedIds.join()
        };
        this.link = `permission-management/user-management/user-group`;
        this.name = 'UserGroup';
        // if(this.fileSelection !== null) {
        //   downLoadFileFromServer(link, params, 'UserGroup', this.fileSelection);
        //   this.fnHideModal('model-export')
        // }
      },
      onPrintGroupButton() {
        let checkedAll = this.$refs.userGroupTable.checkedAllStatus;
        let checkedIds = this.$refs.userGroupTable.selectedTo;
        let httpOption = this.$refs.userGroupTable.httpOptions;
        let params = {
          'locale' : getLocale(),
          'isAll': checkedIds.length > 0 ? checkedAll : true,
          'sort' : httpOption.params.sort,
          'filter': this.groupFilter,
          'idList': checkedIds.join()
        };
        let link = `permission-management/user-management/user-group`;
        printFileFromServer(link, params);
      },

      getAllUserData() {
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
      onCreatePage() { // move to create page
        // reset models
        this.onInitialUserData();
        this.submitted = false;
        this.modifyPage = false;
        // reset create form
        this.$v.profileForm.$reset();
        // change page to create
        this.pageStatus = 'create';
      },

      onGetAll() {
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
      onTableListPage() {
        this.pageStatus = 'table';
      },
      onSaveUserPage() {
        this.submitted = true;
        if (this.profileForm.passwordType === "default") {
          this.profileForm.passwordValue = "s123456";
        }

        this.$v.profileForm.$touch();


        if (this.$v.profileForm.$invalid) {
          if (this.$v.profileForm.userName.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-user-name`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.profileForm.userNumber.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-user-id`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.profileForm.gender.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-select-gender`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.profileForm.identityCard.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-license-number`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.profileForm.orgId.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-select-org`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.profileForm.email.$invalid) {
            if(this.profileForm.email === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-email`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.invalid-enter-email`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.profileForm.mobile.$invalid) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-organization-mobile`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          if (this.$v.profileForm.userAccount.$invalid) {
            if(this.profileForm.userAccount === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-user-account`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.invalid-user-account`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }
          if (this.$v.profileForm.passwordValue.$invalid) {
            if(this.profileForm.passwordValue === '') {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.please-enter-password`), {
                duration: 3000,
                permanent: false
              });
            }
            else if(this.profileForm.passwordValue.length<6 || this.profileForm.passwordValue.length>20) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`password-reset.password-length`), {
                duration: 3000,
                permanent: false
              });
            }
            else {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.password-valid`), {
                duration: 3000,
                permanent: false
              });
            }
            return;
          }

          return;
        }

        const formData = new FormData();
        for (let key in this.profileForm) {


          if (key !== 'portrait' && key !== 'avatar')
            formData.append(key, this.profileForm[key]);
          else if (key === 'portrait' && this.profileForm['portrait'] !== null) {
            formData.append(key, this.profileForm[key], this.profileForm[key].name);
          }
        }
        this.isLoading = true;

        // call api
        let finalLink = this.profileForm.userId > 0 ? 'modify' : 'create';
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/user-management/user/` + finalLink, formData)
          .then((response) => {
            let message = response.data.message;
            let data = response.data.data;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.profileForm.userId > 0 ? this.$t(`permission-management.user-modify-successfully`) : this.$t(`permission-management.user-created-successfully`), {
                  duration: 3000,
                  permanent: false
                });
                this.onInitialUserData();
                this.getAllUserData();
                // back to table
                this.pageStatus = 'table';
                this.$refs.vuetable.reload();
                this.onGetAll();

                break;
              case responseMessages['used-user-account']://duplicated user account
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user-account-already-used`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-email']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.used-email`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-mobile']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.used-phone`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['used-user-number']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.used-number`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
            this.isLoading = false;
            this.$v.profileForm.$reset();
          })
          .catch((error) => {
            this.isLoading = false;
            this.$v.profileForm.$reset();
          });

      },
      onAction(action, data, index) {
        let userId = data.userId;
        switch (action) {
          case 'modify':
            this.modifyPage = true;
            this.fnModifyItem(data);
            break;
          case 'show':
            this.fnShowItem(data);
            break;
          case 'reset-password':
            this.$refs['modal-reset'].show();
            this.promptTemp.userId = userId;
            this.promptTemp.action = action;
            break;
          case 'activate':
            this.promptTemp.userId = userId;
            this.promptTemp.action = action;
          case 'unblock':
            this.showConfDiaglog(userId, action);
            //this.fnChangeItemStatus(userId, action);
            break;
          case 'inactivate':
            this.promptTemp.userId = userId;
            this.promptTemp.action = action;
          // this.fnChangeItemStatus();
          // break;
          case 'blocked':
            this.fnShowConfDiaglog(userId, action);
            break;
          case 'group-remove':
            this.fnShowUserGroupConfDiaglog(data);
            break;
        }
      },

      resetPassword(data) {
        this.$v.passwordForm.$touch();
        if (this.$v.passwordForm.$invalid) {

          if (this.passwordForm.password === null) {
            this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.input-none`), {
              duration: 3000,
              permanent: false
            });
          } else {
            if (!isAccountValid(this.passwordForm.password)) {
              this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.format-invalid`), {
                duration: 3000,
                permanent: false
              });
            } else if(this.passwordForm.password.length<6 || this.passwordForm.password.length>20){
              this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.password-length`), {
                duration: 3000,
                permanent: false
              });
            }
            else if (this.$v.passwordForm.confirmPassword.$invalid) {
              this.$notify('error', this.$t('permission-management.warning'), this.$t(`password-reset.confirm-invalid`), {
                duration: 3000,
                permanent: false
              });
            }
          }
          return;
        }
        getApiManager()
          .post(`${apiBaseUrl}/permission-management/user-management/user/modify-password`, {
            userId: this.promptTemp.userId,
            password: this.passwordForm.password
          })
          .then((response) => {
            let message = response.data.message;
            switch (message) {
              case responseMessages['ok']: // okay
                this.$notify('success', this.$t('permission-management.success'), this.$t(`password-reset.update-password-successful`), {
                  duration: 3000,
                  permanent: false
                });
                this.$refs['modal-reset'].hide();
                this.$refs.vuetable.reload();
                this.passwordForm.password = null;
                this.passwordForm.confirmPassword = null;
                break;
              case responseMessages['user-not-lock']:
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`password-reset.user-not-locked`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          });
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
      showConfDiaglog(userId, action) {
        this.promptTemp.userId = userId;
        this.promptTemp.action = action;
        this.$refs['modal-active'].show();
      },
      fnModifyItem(data) {

        this.onInitialUserData();
        for (let key in this.profileForm) {
          if (Object.keys(data).includes(key)) {
            if (key !== 'portrait' && key !== 'avatar')
              this.profileForm[key] = data[key];
            else if (key === 'portrait')
              this.profileForm.avatar = data['portrait'];
          }
        }
        this.profileForm.portrait = null;

        if (data.password === 'default') {
          this.profileForm.passwordType = 'default';
        } else {
          this.profileForm.passwordType = 'other';
          this.profileForm.passwordValue = data.password;
        }
        this.pageStatus = 'create';
        this.$v.profileForm.$reset();
      },
      fnShowItem(data) {
        this.onInitialUserData();
        for (let key in this.profileForm) {
          if (Object.keys(data).includes(key))
            if (key !== 'portrait' && key !== 'avatar')
              this.profileForm[key] = data[key];
            else if (key === 'portrait')
              this.profileForm.avatar = data['portrait'];
        }

        this.profileForm.portrait = null;

        if (data.password === 'default') {
          this.profileForm.passwordType = 'default';
        } else {
          this.profileForm.passwordType = 'other';
          this.profileForm.passwordValue = data.password;
        }
        this.pageStatus = 'show';
        this.$v.profileForm.$reset();
      },
      fnChangeItemStatus(userId = 0, action = '') {
        if (userId === 0)
          userId = this.promptTemp.userId;
        if (action === '')
          action = this.promptTemp.action;
        let status = action;
        if (status === 'unblock' || status === 'reset-password' || status === 'inactivate')
          status = '1000000302';
        else if (status === 'activate')
          status = '1000000301';
        else if (status === 'blocked')
          status = '1000000303';
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
                this.profileForm.status = status;
                this.$refs.vuetable.reload();
                this.getAllUser();
                break;

              case responseMessages['has-roles']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.has-roles`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-data-groups']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.has-data-group`), {
                  duration: 3000,
                  permanent: false
                });
                break;
              case responseMessages['has-user-groups']://duplicated user email
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.has-user-groups`), {
                  duration: 3000,
                  permanent: false
                });
                break;
            }
          })
          .catch((error) => {
          })
          .finally(() => {
            this.$refs['modal-prompt'].hide();
            this.$refs['modal-active'].hide();
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
          orgId: null,
          gender: null
        };
        // if (this.defaultOrgId !== '')
        //   this.filter.orgId = this.defaultOrgId;
      },
      onInitialUserData() {
        this.profileForm = {
          status: '1000000102',
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
          temp.orgName = temp.org.orgName;
          transformed.data.push(temp);
          this.renderedCheckList.push(data.data[i].userId);
        }

        return transformed

      },
      //second tab content
      fnShowUserGroupConfDiaglog(userGroupItem) {
        this.selectedId = userGroupItem.userGroupId;
        this.$refs['modal-prompt-group'].show();
      },
      fnDeleteUserGroupItem() {
        if (this.selectedId > 0) {
          this.$refs['modal-prompt-group'].hide();
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/user-management/user-group/delete`, {
              userGroupId: this.selectedId
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
                case responseMessages['has-users']: // okay
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-users`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['has-roles']: // okay
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.group-has-roles`), {
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
          transformed.data.push(temp);
          let usersName =[];
          temp.users.forEach(users => {
            usersName.push(users.userName);
          });
          let groupMember = usersName.join(',');
          let isLong = false;
          if(groupMember.length>20){
            isLong = true;
            temp.groupMember = {
              groupMember : groupMember,
              label : groupMember.substr(0, 19) + '...',
              isLong : isLong
            };
          }
          else {
            temp.groupMember = {
              groupMember : groupMember,
              isLong : isLong
            };
          }
          // if(groupMember.length>30){
          //   groupMember = groupMember.substr(0, 30) + "···"; // Gets the first part
          // }
          //temp.groupMember = groupMember;

          this.renderedCheckListGroup.push(data.data[i].userGroupId);
        }

        return transformed

      },
      userTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedCheckList = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.vuetableItems.perPage,
          sort: httpOptions.params.sort,
          filter: {
            userName: this.filter.userName,
            status: this.filter.status,
            orgId: this.filter.orgId,
            gender: this.filter.gender,
          }
        });
      },
      onUserTablePaginationData(paginationData) {
        this.$refs.pagination.setPaginationData(paginationData);
        this.changeCheckAllStatus();
      },
      onUserTableChangePage(page) {
        this.$refs.vuetable.changePage(page);
        this.changeCheckAllStatus();
      },
      userGroupTableHttpFetch(apiUrl, httpOptions) { // customize data loading for table from server
        this.renderedCheckListGroup = [];
        return getApiManager().post(apiUrl, {
          currentPage: httpOptions.params.page,
          perPage: this.userGroupTableItems.perPage,
          sort: httpOptions.params.sort,
          filter: this.groupFilter
        });
      },
      onUserGroupTablePaginationData(paginationData) {
        this.$refs.userGroupPagination.setPaginationData(paginationData);
        this.changeCheckAllStatusGroup();
      },
      onUserGroupTableChangePage(page) {
        this.$refs.userGroupTable.changePage(page);
        this.changeCheckAllStatusGroup();
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
        console.log(this.selectedUserGroupItem);
        this.groupForm.status = 'modify';
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
        //log(this.orgUserTreeData);
        this.getTreeData(this.orgUserTreeData, 0);

      },
      getTreeData(treeData, index) {
        // var str = "";
        // for(var i = 0; i < index * 2; i ++) str = str + "-";

        if (!treeData || treeData.length === 0) {
          return;
        }
        let tmp = treeData;
        var answer = [];
        for (let i = tmp.length - 1; i >= 0; i--) {

          if (tmp[i].userId != null && tmp[i].userId != undefined) {
            continue;
          }
          this.getTreeData(tmp[i].children, index + 1);
          if (!tmp[i].children || tmp[i].children.length == 0) {
            tmp.splice(i, 1);
          }
        }
        return;


        //
        // let array = [];
        // for (let b in a.children) {
        //   if(this.isInvalid(b)) array.push(b)
        // }
        // a.children = array;
        // //array.size = 0
        // return a;
      },

      onUserGroupSearchButton() {
        this.$refs.userGroupTable.refresh();
      },
      onUserGroupResetButton() {
        this.groupFilter = {
          groupName: null,
          userName:null
        };
      },
      onUserGroupCreateButton() {
        this.isSelectedAllUsersForDataGroup = false;
        this.selectedUserGroupItem = {
          users: []
        };
        let groupNumberStr = "PG";
        for (let i = 0; i < 8; i++) {
          let index = Math.floor(Math.random() * 10);
          groupNumberStr = groupNumberStr + index.toString();
        }
        this.groupForm = {
          groupNumber: groupNumberStr,
          groupName: null,
          status: 'create'
        }
      },
      onClickDeleteUserGroup() {
        this.fnShowUserGroupConfDiaglog(this.selectedUserGroupItem);
      },
      onClickCreateUserGroup() {

        if (this.selectedUserGroupItem) {
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let userGroupUserIds = [];
          checkedNodes.forEach((node) => {
            if (node.isUser) userGroupUserIds.push(node.userId);
          });
          if (this.$v.groupForm.$invalid) {
            if (this.$v.groupForm.groupNumber.$invalid) {
              if (this.groupForm.groupNumber === '') {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.user-groupId-field-is-mandatory`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              } else {
                this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.user-groupId-field-invalid-format`), {
                  duration: 3000,
                  permanent: false
                });
                return;
              }
            }
            if (this.$v.groupForm.groupName.$invalid) {
              this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.user-group-name-field-is-mandatory`), {
                duration: 3000,
                permanent: false
              });
              return;
            }
            return;
          }
          if (userGroupUserIds.length === 0) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.required-user`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          getApiManager()
            .post(`${apiBaseUrl}/permission-management/user-management/user-group/create`, {
              'groupName': this.groupForm.groupName,
              'groupNumber': this.groupForm.groupNumber,
              'userIdList': userGroupUserIds
            })
            .then((response) => {
              let message = response.data.message;
              let data = response.data.data;
              switch (message) {
                case responseMessages['ok']:
                  this.$notify('success', this.$t('permission-management.success'), this.$t(`permission-management.user.user-group-created-successfully`), {
                    duration: 3000,
                    permanent: false
                  });
                  this.$refs.userGroupTable.refresh();
                  this.groupForm = {
                    groupName: null,
                    groupNumber: null,
                    status: 'create'
                  };
                  break;
                case responseMessages['used-user-group-name']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-name`), {
                    duration: 3000,
                    permanent: false
                  });
                  break;
                case responseMessages['used-user-group-number']:
                  this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-number`), {
                    duration: 3000,
                    permanent: false
                  });
                  let groupNumberStr = "PG";
                  for (let i = 0; i < 8; i++) {
                    let index = Math.floor(Math.random() * 10);
                    groupNumberStr = groupNumberStr + index.toString();
                  }
                  this.groupForm = {
                    groupNumber: groupNumberStr,
                    groupName: null,
                    status: 'create'
                  }
                  break;
                default:

              }
            })
            .catch((error) => {

            }).finally(() => {
            //

          });
        }
      },
      onClickModifyUserGroup() {
        if (this.selectedUserGroupItem) {
          if (this.selectedUserGroupItem.groupName === '') {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.user-group-name-field-is-mandatory`), {
              duration: 3000,
              permanent: false
            });
            return;
          }
          let checkedNodes = this.$refs.orgUserTree.getCheckedNodes();
          let userGroupUserIds = [];
          checkedNodes.forEach((node) => {
            if (node.isUser) userGroupUserIds.push(node.userId);
          });

          if (userGroupUserIds.length === 0) {
            this.$notify('warning', this.$t('permission-management.warning'), this.$t(`permission-management.user.required-user`), {
              duration: 3000,
              permanent: false
            });
          } else {
            getApiManager()
              .post(`${apiBaseUrl}/permission-management/user-management/user-group/modify`, {
                'userGroupId': this.selectedUserGroupItem.userGroupId,
                'groupName': this.selectedUserGroupItem.groupName,
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
                    this.$refs.userGroupTable.reload();
                    break;
                  case responseMessages['used-user-group-name']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-name`), {
                      duration: 3000,
                      permanent: false
                    });
                    break;
                  case responseMessages['used-user-group-number']:
                    this.$notify('warning', this.$t('permission-management.warning'), this.$t(`response-error-message.used-user-group-number`), {
                      duration: 3000,
                      permanent: false
                    });
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
  }
</script>
