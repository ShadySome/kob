<template>
  <NavBar></NavBar>
  <router-view/>
</template>
<script>
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";

import NavBar from './components/NavBar.vue';
export default{
  name:"App",
  components:{
    NavBar,
  },

}
</script>
<style >
body{
  background-image: url("./assets/image/bg.jpg");
  background-size: cover;
}
</style>
