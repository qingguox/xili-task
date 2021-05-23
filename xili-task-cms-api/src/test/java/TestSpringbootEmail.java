//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = Application.class)
public class TestSpringbootEmail {
//
//    @Resource
//    MailSendUtils mainUtils;
//
//    @Resource
//    TemplateEngine templateEngine;
//
//    /**
//     *  简单文本邮件发送
//     */
//    @Test
//    public void sendSimpleMailTest(){
//        mainUtils.sendSimpleMail("1605694825@qq.com","简单文本邮件","这是我的第一封邮件,哈哈...");
//    }
//
//    /**
//     *  HTML邮件发送
//     *
//     * @throws Exception
//     */
//    @Test
//    public void sendHtmlMailTest() throws Exception{
//
//        String content = "<html>\n"+
//                        "<body>\n" +
//                            "<h1 style=\"color: red\"> hello world , 这是一封HTML邮件</h1>"+
//                        "</body>\n"+
//                        "</html>";
//
//
//        mainUtils.sendHtmlMail("1605694825@qq.com","Html邮件发送",content);
//    }
//
//    /**
//     *  发送副本邮件
//     *
//     * @throws Exception
//     */
//    @Test
//    public void sendAttachmentMailTest() throws Exception{
//        String filepath = "/C:/Users/chaiyachun/Desktop/教资资料.txt";
//
//        mainUtils.sendAttachmentMail("chaiyc89@126.com","发送副本","这是一篇带附件的邮件",filepath);
//
//    }
//
//    /**
//     *  发送图片邮件
//     *
//     * @throws Exception
//     */
//    @Test
//    public void sendImageMailTest() throws Exception{
//        //发送多个图片的话可以定义多个 rscId,定义多个img标签
//
//        String filePath = "/C:/Users/chaiyachun/Desktop/test.png";
//        String rscId = "chaiyachun001";
//        String content = "<html><body> 这是有图片的邮件: <img src=\'cid:"+rscId+"\'> </img></body></html>";
//
//        mainUtils.sendImageMail("chaiyc89@126.com","这是一个带图片的邮件",content,filePath,rscId);
//    }
//
//    /**
//     *  发送邮件模板
//     *
//     * @throws Exception
//     */
//    @Test
//    public void sendTemplateEmailTest() throws Exception {
//        Context context = new Context();
//        context.setVariable("id","006");
//        String emailContent = templateEngine.process("templates",context);
//        mainUtils.sendHtmlMail("chaiyc89@126.com","这是一个模板文件",emailContent);
//
    //    }
//

}