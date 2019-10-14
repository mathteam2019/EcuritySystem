<template>
  <div>
    <b-row>
      <b-colxx xxs="12">
        <piaf-breadcrumb :heading="$t('menu.parameter-setting')"/>
        <div class="separator mb-5"></div>
      </b-colxx>
    </b-row>

    <b-form @submit.prevent="onHorizontalSubmit">

      <b-row>
        <b-col cols="6">
          <b-row>
            <b-col cols="12">
              <b-card class="mb-4">
                <b-row>
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.standby-time')">
                      <b-input-group :append="$t('system-setting.minutes')">
                        <b-form-input v-model="formData.standbyTime" type="number" min="0" />
                      </b-input-group>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-card>
            </b-col>
            <b-col cols="12">
              <b-card class="mb-4">
                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.auto-recognition')">
                      <switches theme="custom" color="primary" v-model="formData.autoRecognition"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.recognition-rate')">
                      <b-form-input v-model="formData.recognitionRate" type="number" min="-1" max="1"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.atr-rect-color')">
                      <b-form-input v-model="formData.atrRectColor"></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.record-delete-rect')">
                      <switches theme="custom" color="primary" v-model="formData.recordDeleteRect"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-card>
            </b-col>
            <b-col cols="12">
              <b-card class="mb-4">
                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.check-disk-period')">
                      <b-input-group :append="$t('system-setting.minutes')">
                        <b-form-input v-model="formData.checkDiskPeriod" type="number" min="0"></b-form-input>
                      </b-input-group>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.warn-disk-space')">
                      <b-input-group append="G">
                        <b-form-input v-model="formData.warnDiskSpace" type="number" min="0"></b-form-input>
                      </b-input-group>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.history-image-delete-period')">
                      <b-input-group :append="$t('system-setting.hours')">
                        <b-form-input v-model="formData.historyImageDeletePeriod" type="number" min="0"></b-form-input>
                      </b-input-group>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.save-scan-data')">
                      <switches theme="custom" color="primary" v-model="formData.saveScanData"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row >
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.save-suspect-data')">
                      <switches theme="custom" color="primary" v-model="formData.saveSuspectData"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-card>
            </b-col>
          </b-row>
        </b-col>
        <b-col cols="6">
          <b-row>
            <b-col cols="12">
              <b-card class="mb-4">
                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.alarm-sound')">
                      <switches theme="custom" color="primary" v-model="formData.alarmSound"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.pass-sound')">
                      <switches theme="custom" color="primary" v-model="formData.passSound"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.pos-error-sound')">
                      <switches theme="custom" color="primary" v-model="formData.posErrorSound"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.start-sound')">
                      <switches theme="custom" color="primary" v-model="formData.startSound"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.scan-sound')">
                      <switches theme="custom" color="primary" v-model="formData.scanSound"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.scan-complete-sound')">
                      <switches theme="custom" color="primary" v-model="formData.scanCompleteSound"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>
              </b-card>
            </b-col>
            <b-col cols="12">
              <b-card class="mb-4">
                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.facial-blurring')">
                      <switches theme="custom" color="primary" v-model="formData.facialBlurring"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.chest-blurring')">
                      <switches theme="custom" color="primary" v-model="formData.chestBlurring"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3">
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.hip-blurring')">
                      <switches theme="custom" color="primary" v-model="formData.hipBlurring"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="12">
                    <b-form-group label-cols="4" horizontal :label="$t('system-setting.groin-blurring')">
                      <switches theme="custom" color="primary" v-model="formData.groinBlurring"></switches>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col class="d-flex justify-content-end">
                    <b-button type="submit" variant="info" class="mt-4">{{ $t('system-setting.save') }}</b-button>
                  </b-col>
                </b-row>
              </b-card>
            </b-col>
          </b-row>
        </b-col>
      </b-row>
    </b-form>
  </div>
</template>

<script>
    import _ from 'lodash';
    import InputTag from '../../../components/Form/InputTag';
    import vSelect from 'vue-select'
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
    import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
    import Switches from 'vue-switches'
    import 'vue-select/dist/vue-select.css'

    export default {
        components: {
            'input-tag' : InputTag,
            'v-select' : vSelect,
            'vuetable' : Vuetable,
            'vuetable-pagination': VuetablePagination,
            'vuetable-pagination-bootstrap' : VuetablePaginationBootstrap,
            'switches' : Switches,
        },
        data () {
            return {
                switchValue: false,
                formData: {
                    standbyTime: 0,
                    alarmSound: true,
                    passSound: true,
                    posErrorSound: true,
                    startSound: false,
                    scanSound: false,
                    scanCompleteSound: false,
                    autoRecognition: true,
                    recognitionRate: 0,
                    atrRectColor: '#ffffff',
                    recordDeleteRect: true,
                    checkDiskPeriod: 60,
                    warnDiskSpace: 15,
                    historyImageDeletePeriod: 0,
                    saveScanData: true,
                    saveSuspectData: false,
                    facialBlurring: true,
                    chestBlurring: true,
                    hipBlurring: true,
                    groinBlurring: true,
                }
            }
        },
        methods: {
            onHorizontalSubmit() {
                console.log('submit form');
            },
        }
    }
</script>

