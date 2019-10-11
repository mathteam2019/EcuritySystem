
const data = [{
    id: "piaf",
    icon: "iconsminds-shop",
    label: "menu.piaf",
    to: "/app/piaf",
    subs: [{
        icon: "simple-icon-paper-plane",
        label: "menu.start",
        to: "/app/piaf/start"
    },
    ]
},
{
    id: "second-menu",
    icon: "iconsminds-chemical",
    label: "menu.second-menu",
    to: "/app/second-menu",
    subs: [{
        icon: "simple-icon-paper-plane",
        label: "menu.second",
        to: "/app/second-menu/second"
    },
    ]
},
{
    id: "pages",
    icon: "iconsminds-digital-drawing",
    label: "menu.pages",
    to: "/user/login",
    subs: [{
        icon: "simple-icon-user-following",
        label: "menu.login",
        to: "/user/login",
        newWindow: true
    },{
        icon: "simple-icon-user-follow",
        label: "menu.register",
        to: "/user/register",
        newWindow: true
    },{
        icon: "simple-icon-user-unfollow",
        label: "menu.forgot-password",
        to: "/user/forgot-password",
        newWindow: true
    },
    ]
},
{
    id: "single",
    icon: "iconsminds-three-arrow-fork",
    label: "menu.single",
    to: "/app/single"
},
{
    id: "docs",
    icon: "iconsminds-library",
    label: "menu.docs",
    to: "https://piaf-vue-docs.coloredstrategies.com/",
    newWindow: true
}
];
export default data;