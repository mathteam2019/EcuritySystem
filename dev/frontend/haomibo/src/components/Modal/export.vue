<script>
  import vSelect from 'vue-select';
  import 'vue-select/dist/vue-select.css'
  import {getDirection} from "../../utils";
  import {validationMixin} from "vuelidate";
  import {downLoadFileFromServer} from "../../api";

  const {required, email, minLength, maxLength, alphaNum} = require('vuelidate/lib/validators');

  export default {
    name: 'modal',
    props: {
      show: {
        type: Boolean,
        default: false
      },
      link: {
        type: String
      },
      params: {
        type: Object
      },
      name: {
        type: String
      }
    },
    components: {
      'v-select': vSelect,
    },
    mixins: [validationMixin],
    validations: {
      fileSelection: {
        required
      },
    },

    data() {
      return {
        fileSelection: [],
        direction: getDirection().direction,
        fileSelectionOptions: [
          {value: 'docx', label: 'WORD'},
          {value: 'xlsx', label: 'EXCEL'},
          {value: 'pdf', label: 'PDF'},
        ],
      }
    },

    methods: {
      exportFile() {
        if (this.fileSelection !== null) {
          downLoadFileFromServer(this.link, this.params, this.name, this.fileSelection);
          this.close();
        }
      },
      close() {
        this.$emit('close');
      },

    },
  };
</script>

<template>
  <div class="modal fade show modal-open modal" style="display:block;">
    <div class="modal-dialog modal-md modal-dialog-centered">
      <div class="modal-content">
        <header class="modal-header">
          <slot name="header" class="modal-title">
            <label class="modal-title">
            {{ $t('permission-management.export') }}
            </label>

            <button
              type="button"
              class="btn-close"
              @click="close"
            >
              x
            </button>
          </slot>
        </header>
        <section class="modal-body">
          <slot name="body">
            <b-form-group class="mw-100 w-100" :label="$t('permission-management.export')">
              <v-select v-model="fileSelection" :options="fileSelectionOptions"
                        :state="!$v.fileSelection.$invalid"
                        class="v-select-custom-style" :dir="direction" multiple/>
            </b-form-group>
          </slot>
        </section>
        <footer class="modal-footer">
          <slot name="footer">
            <b-button size="sm" variant="orange default" @click="exportFile">
              <i class="icofont-gift"/>
              {{ $t('permission-management.export') }}
            </b-button>
            <b-button size="sm" variant="light default" @click="close">
              <i class="icofont-long-arrow-left"/>{{$t('system-setting.cancel')}}
            </b-button>
          </slot>
        </footer>
      </div>
    </div>
  </div>
</template>

<style>
  /*.modal-backdrop {*/
  /*  position: fixed;*/
  /*  top: 0;*/
  /*  bottom: 0;*/
  /*  left: 0;*/
  /*  right: 0;*/
  /*  background-color: rgba(0, 0, 0, 0.3);*/
  /*  display: flex;*/
  /*  justify-content: center;*/
  /*  align-items: center;*/
  /*}*/

  /*.modal-backdrop {*/
  /*  opacity: 1;*/
  /*}*/

  /*.modal {*/
  /*  background: #FFFFFF;*/
  /*  box-shadow: 2px 2px 20px 1px;*/
  /*  overflow-x: auto;*/
  /*  display: flex;*/
  /*  flex-direction: column;*/
  /*}*/

  /*.modal-header,*/
  /*.modal-footer {*/
  /*  padding: 15px;*/
  /*  display: flex;*/
  /*}*/

  /*.modal-header {*/
  /*  border-bottom: 1px solid #eeeeee;*/
  /*  color: #4AAE9B;*/
  /*  justify-content: space-between;*/
  /*}*/

  /*.modal-footer {*/
  /*  border-top: 1px solid #eeeeee;*/
  /*  justify-content: flex-end;*/
  /*}*/

  /*.modal-body {*/
  /*  position: relative;*/
  /*  padding: 20px 10px;*/
  /*}*/

  .btn-close {
    border: none;
    font-size: 20px;
    padding: 20px;
    cursor: pointer;
    font-weight: bold;
    color: #4AAE9B;
    background: transparent;
  }

  .btn-green {
    color: white;
    background: #4AAE9B;
    border: 1px solid #4AAE9B;
    border-radius: 2px;
  }


  .modal-open {
    overflow: hidden
  }

  .modal-open .modal {
    overflow-x: hidden;
    overflow-y: auto
  }

  .modal {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1050;
    display: none;
    width: 100%;
    height: 100%;
    overflow: hidden;
    outline: 0
  }

  .modal-dialog {
    position: relative;
    width: auto;
    margin: .5rem;
    pointer-events: none
  }

  .modal.fade .modal-dialog {
    -webkit-transition: -webkit-transform .3s ease-out;
    transition: -webkit-transform .3s ease-out;
    transition: transform .3s ease-out;
    transition: transform .3s ease-out, -webkit-transform .3s ease-out;
    -webkit-transform: translateY(-50px);
    transform: translateY(-50px)
  }

  @media (prefers-reduced-motion: reduce) {
    .modal.fade .modal-dialog {
      -webkit-transition: none;
      transition: none
    }
  }

  .modal.show .modal-dialog {
    -webkit-transform: none;
    transform: none
  }

  .modal-dialog-scrollable {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    max-height: calc(100% - 1rem)
  }

  .modal-dialog-scrollable .modal-content {
    max-height: calc(100vh - 1rem);
    overflow: hidden
  }

  .modal-dialog-scrollable .modal-footer, .modal-dialog-scrollable .modal-header {
    -ms-flex-negative: 0;
    flex-shrink: 0
  }

  .modal-dialog-scrollable .modal-body {
    overflow-y: auto
  }

  .modal-dialog-centered {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    min-height: calc(100% - 1rem)
  }

  .modal-dialog-centered:before {
    display: block;
    height: calc(100vh - 1rem);
    content: ""
  }

  .modal-dialog-centered.modal-dialog-scrollable {
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    justify-content: center;
    height: 100%
  }

  .modal-dialog-centered.modal-dialog-scrollable .modal-content {
    max-height: none
  }

  .modal-dialog-centered.modal-dialog-scrollable:before {
    content: none
  }

  .modal-content {
    position: relative;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-orient: vertical;
    -webkit-box-direction: normal;
    -ms-flex-direction: column;
    flex-direction: column;
    width: 100%;
    pointer-events: auto;
    background-color: #fff;
    background-clip: padding-box;
    border: 1px solid rgba(0, 0, 0, .2);
    border-radius: .3rem;
    outline: 0
  }

  .modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 1040;
    width: 100vw;
    height: 100vh;
    background-color: #000
  }

  .modal-backdrop.fade {
    opacity: 0
  }

  .modal-backdrop.show {
    opacity: .5
  }

  .modal-header {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: start;
    -ms-flex-align: start;
    align-items: flex-start;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    padding: 1rem 1rem;
    border-bottom: 1px solid #dee2e6;
    border-top-left-radius: .3rem;
    border-top-right-radius: .3rem
  }

  .modal-header .close {
    padding: 1rem 1rem;
    margin: -1rem -1rem -1rem auto
  }

  .modal-title {
    margin-bottom: 0;
    line-height: 1.5
  }

  .modal-body {
    position: relative;
    -webkit-box-flex: 1;
    -ms-flex: 1 1 auto;
    flex: 1 1 auto;
    padding: 1rem
  }

  .modal-footer {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: end;
    -ms-flex-pack: end;
    justify-content: flex-end;
    padding: 1rem;
    border-top: 1px solid #dee2e6;
    border-bottom-right-radius: .3rem;
    border-bottom-left-radius: .3rem
  }

  .modal-footer > :not(:first-child) {
    margin-left: .25rem
  }

  .modal-footer > :not(:last-child) {
    margin-right: .25rem
  }

  .modal-scrollbar-measure {
    position: absolute;
    top: -9999px;
    width: 50px;
    height: 50px;
    overflow: scroll
  }

  @media (min-width: 576px) {
    .modal-dialog {
      max-width: 500px;
      margin: 1.75rem auto
    }

    .modal-dialog-scrollable {
      max-height: calc(100% - 3.5rem)
    }

    .modal-dialog-scrollable .modal-content {
      max-height: calc(100vh - 3.5rem)
    }

    .modal-dialog-centered {
      min-height: calc(100% - 3.5rem)
    }

    .modal-dialog-centered:before {
      height: calc(100vh - 3.5rem)
    }

    .modal-sm {
      max-width: 300px
    }
  }

  @media (min-width: 992px) {
    .modal-lg, .modal-xl {
      max-width: 800px
    }
  }

  @media (min-width: 1200px) {
    .modal-xl {
      max-width: 1140px
    }
  }

</style>
