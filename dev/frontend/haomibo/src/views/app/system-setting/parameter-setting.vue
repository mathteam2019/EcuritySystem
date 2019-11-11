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
      <b-tab :title="$t('system-setting.parameter-setting.platform-parameter')">

        <div class="section pt-0 mx-3">
          <b-tabs class="sub-tabs" card>
            <b-tab :title="$t('menu.personal-inspection')">
              <div>

                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.scan')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.atr-suspect-box-color')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.judgement')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.dispatch-timeout')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.processing-timeout-period')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.work-timeout-reminder')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.judgement-frame-color')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="1">
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.history-task')}}</label>
                  </b-col>
                  <b-col cols="2">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-storage')">
                      <b-form-select plain></b-form-select>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.data-output')">
                      <b-form-select plain></b-form-select>
                    </b-form-group>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.seized-item-classification')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.seized-items')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.check-item-level')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col>
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.hand-process-history')}}</label>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.show-deleted-suspected-box')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col>
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.hand-process-history')}}</label>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group class="mb-0" :label="$t('system-setting.parameter-setting.deleted-suspected-box-color')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>
              </div>
            </b-tab>
            <b-tab :title="$t('permission-management.other')">
              <div>
                <b-row class="mb-3">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('menu.permission-management')}}
                        </span>
                      </b-col>
                      <b-col><hr /></b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('permission-management.password')">
                      <b-form-input type="password"></b-form-input>
                    </b-form-group>
                  </b-col>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.login-fail-count')">
                      <label>7</label>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3 mt-2">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('menu.log-management')}}
                        </span>
                      </b-col>
                      <b-col><hr /></b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.log-export-number')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3 mt-2">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('system-setting.parameter-setting.business-statistics')}}
                        </span>
                      </b-col>
                      <b-col><hr /></b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.security-instrument-flow-setting')">
                      <b-form-select plain></b-form-select>
                    </b-form-group>
                  </b-col>
                </b-row>

                <b-row class="mb-3 mt-2">
                  <b-col cols="7">
                    <b-row>
                      <b-col cols="auto" class="d-flex align-items-center">
                        <span class="font-weight-bold">
                          {{$t('permission-management.other')}}
                        </span>
                      </b-col>
                      <b-col><hr /></b-col>
                    </b-row>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col>
                    <label class="font-weight-bold">{{$t('system-setting.parameter-setting.platform-security-instrument')}}</label>
                  </b-col>
                </b-row>

                <b-row>
                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.storage-check-period')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.storage-warning-size')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>

                  <b-col cols="2" offset="1">
                    <b-form-group :label="$t('system-setting.parameter-setting.history-save-period')">
                      <b-form-input></b-form-input>
                    </b-form-group>
                  </b-col>
                </b-row>

              </div>
            </b-tab>
          </b-tabs>
        </div>

        <div class="text-right mr-3 mt-3">
          <b-button size="sm" variant="info default" class="mr-3">
            <i class="icofont-save"></i>
            {{$t('permission-management.permission-control.save')}}
          </b-button>
          <b-button size="sm" variant="info default">
            <i class="icofont-long-arrow-left"></i>
            {{$t('permission-management.return')}}
          </b-button>
        </div>

      </b-tab>

      <b-tab :title="$t('system-setting.parameter-setting.security-instrument')">
      </b-tab>
    </b-tabs>
  </div>
</template>

<style lang="scss">
  $internal-tab-content-height-offset: 400px;

  .vc-chrome {
    position: absolute;
    top: 35px;
    right: 0;
    z-index: 9;
  }
  .current-color {
    display: inline-block;
    width: 16px;
    height: 16px;
    background-color: #000;
    cursor: pointer;
  }

  .section.pt-0 {

    background-color: #f3f3f3;
    padding-bottom: 0px!important;

    .nav-tabs .nav-link {
      &.active {
        box-shadow: none!important;
        border-left: 1px #cccccc solid;
        border-right: 1px #cccccc solid;

        &::before {
          height: 0px;
        }
      }
      &:not(.active) {
        background-color: transparent!important;
      }
    }

    .card-header-tabs {
      margin-left: calc(-1.25rem - 1px);
    }

    .tab-content {
      box-shadow: none!important;
      border-top: 1px #cccccc solid;
      border-top-left-radius: 0px!important;
      border-top-right-radius: 0px!important;
      padding-top: 0px!important;
      height: calc(100vh - 337px);
      overflow: auto;

      .font-weight-bold {
        color: #acacac;
      }

    }
  }
</style>

<script>
    import InputTag from '../../../components/Form/InputTag';
    import vSelect from 'vue-select'
    import Vuetable from 'vuetable-2/src/components/Vuetable'
    import VuetablePagination from 'vuetable-2/src/components/VuetablePagination'
    import VuetablePaginationBootstrap from '../../../components/Common/VuetablePaginationBootstrap'
    import Switches from 'vue-switches'
    import Chrome from 'vue-color/src/components/Chrome'
    import 'vue-select/dist/vue-select.css'
    import Vue from 'vue';

    Vue.component('colorpicker', {
        components: {
            'chrome-picker': Chrome,
        },
        template: `

            <b-input-group class="mb-3" ref="colorpicker">
              <b-form-input v-model="colorValue" @focus="showPicker()" @input="updateFromInput" />
              <b-input-group-text>
                <span class="current-color" :style="'background-color: ' + colorValue" @click="togglePicker()"></span>
                <chrome-picker :value="colors" @input="updateFromPicker" v-if="displayPicker" />
              </b-input-group-text>
            </b-input-group>
`,
        props: ['color'],
        data() {
            return {
                colors: {
                    hex: '#000000',
                },
                colorValue: '',
                displayPicker: false,
            }
        },
        mounted() {
            this.setColor(this.color || '#000000');
        },
        methods: {
            setColor(color) {
                this.updateColors(color);
                this.colorValue = color;
            },
            updateColors(color) {
                if(color.slice(0, 1) == '#') {
                    this.colors = {
                        hex: color
                    };
                }
                else if(color.slice(0, 4) == 'rgba') {
                    var rgba = color.replace(/^rgba?\(|\s+|\)$/g,'').split(','),
                        hex = '#' + ((1 << 24) + (parseInt(rgba[0]) << 16) + (parseInt(rgba[1]) << 8) + parseInt(rgba[2])).toString(16).slice(1);
                    this.colors = {
                        hex: hex,
                        a: rgba[3],
                    }
                }
            },
            showPicker() {
                document.addEventListener('click', this.documentClick);
                this.displayPicker = true;
            },
            hidePicker() {
                document.removeEventListener('click', this.documentClick);
                this.displayPicker = false;
            },
            togglePicker() {
                this.displayPicker ? this.hidePicker() : this.showPicker();
            },
            updateFromInput() {
                this.updateColors(this.colorValue);
            },
            updateFromPicker(color) {
                this.colors = color;
                if(color.rgba.a == 1) {
                    this.colorValue = color.hex;
                }
                else {
                    this.colorValue = 'rgba(' + color.rgba.r + ', ' + color.rgba.g + ', ' + color.rgba.b + ', ' + color.rgba.a + ')';
                }
            },
            documentClick(e) {
                var el = this.$refs.colorpicker,
                    target = e.target;
                if(el !== target && !el.contains(target)) {
                    this.hidePicker()
                }
            }
        },
        watch: {
            colorValue(val) {
                if(val) {
                    this.updateColors(val);
                    this.$emit('input', val);
                    //document.body.style.background = val;
                }
            }
        },
    });

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
                showColorPicker: false,
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
                    atrRectColor: {
                        hex: '#FF0000',
                    },
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
            toggleColorPicker() {
                this.showColorPicker = !this.showColorPicker;
            },
            onHorizontalSubmit() {
                console.log('submit form');
            },
        }
    }
</script>

