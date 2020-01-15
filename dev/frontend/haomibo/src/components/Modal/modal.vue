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
  <div class="modal-backdrop" style="display:block;">
    <div class="modal modal-select">
      <header class="modal-header header-font">
        <slot name="header">
          <label class="text-center text-large">
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
          <b-form-group class="mw-100 w-100 body-padding" :label="$t('permission-management.export')">
            <v-select v-model="fileSelection" :options="fileSelectionOptions"
                      :state="!$v.fileSelection.$invalid"
                      class="v-select-custom-style" :dir="direction" multiple/>
          </b-form-group>
        </slot>
      </section>
      <footer class="modal-footer">
        <slot name="footer">
          <b-button size="sm" variant="info default" @click="exportFile">
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
</template>

<style>
  .modal-backdrop {
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3);
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .modal-backdrop {
    opacity: 1;
  }

  .modal-select {
    top: calc((100% - 300px)/2) !important;
    left: calc((100% - 500px)/2) !important;
    background: #FFFFFF;
    box-shadow: 2px 2px 20px 1px;
    overflow-x: auto;
    display: flex;
    flex-direction: column;
    max-width: 500px;
    max-height: 300px;
  }

  .modal-header,
  .modal-footer {
    padding: 15px;
    display: flex;
  }

  .modal-header {
    border-bottom: 1px solid #eeeeee;
    justify-content: space-between;
  }

  .header-font{
    font-weight: bold;
    font-size: large;
  }

  .modal-footer {
    border-top: 1px solid #eeeeee;
    justify-content: flex-end;
  }

  .modal-body {
    position: relative;
    padding: 20px 10px;
  }

  .body-padding {
    padding-right: 2rem;
    padding-left: 2rem;
  }

  .btn-close {
    border: none;
    font-size: 18px;
    cursor: pointer;
    font-weight: bold;
    color: #000000;
    background: transparent;
  }

  .btn-green {
    color: white;
    background: #000000;
    border: 1px solid #000000;
    border-radius: 2px;
  }

</style>
