package main

import (
	"fmt"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

type ReadChaincode struct {
}

// 报名用户
//type EnrollUser struct {
//	EnrollId   string `json:"enrollId"`
//	Cellphone  string `json:"cellphone"`
//	Username   string `json:"username"`
//	Period     string `json:"period"`
//	Status     int    `json:"status"`
//	EnrollTime int16  `json:"enrollTime"`
//}

// 文章实体
//type Article struct {
//	ArticleId    string `json:"articleId"`
//	Period       string `json:"period"`
//	ArticleTitle string `json:"articleTitle"`
//	ArticleUrl   string `json:"articleUrl"`
//	Username     string `json:"username"`
//	Cellphone    string `json:"cellphone"`
//	Stars        int    `json:"stars"`
//	Ctime        int16  `json:"ctime"`
//	Utime        int16  `json:"utime"`
//}

// 评论实体
//type Comment struct {
//	CommentId string `json:"commentId"`
//	ArticleId string `json:"articleId"`
//	Period    string `json:"period"`
//	Cellphone string `json:"cellphone"`
//	Star      int    `json:"star"`
//	Ctime     int16  `json:"ctime"`
//}

// 初始化
func (t *ReadChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ReadChaincode Init")
	return shim.Success(nil)
}

// 方法调用入口
func (t *ReadChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("ReadChaincode Invoke")
	function, args := stub.GetFunctionAndParameters()

	if function != "invoke" {
		return shim.Error("Unknown function call")
	}

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting at least 2")
	}

	if args[0] == "enrollByPeriod" {
		// Deletes an entity from its state
		return t.enrollByPeriod(stub, args)
	}

	if args[0] == "enrollListByPeriod" {
		return t.enrollListByPeriod(stub, args)
	}

	if args[0] == "insertArticle" {
		return t.insertArticle(stub, args)
	}

	if args[0] == "updateArticle" {
		return t.updateArticle(stub, args)
	}

	if args[0] == "queryMyArticleByPeriod" {
		return t.queryMyArticleByPeriod(stub, args)
	}

	if args[0] == "listArticleByPeriod" {
		return t.listArticleByPeriod(stub, args)
	}

	if args[0] == "commentByArticleId" {
		return t.commentByArticleId(stub, args)
	}

	if args[0] == "listCommentByArticleId" {
		return t.listCommentByArticleId(stub, args)
	}

	if args[0] == "getCommentCountByUserId" {
		return t.getCommentCountByUserId(stub, args)
	}
	return shim.Error("Invalid invoke function name. " + function)
}

// 当前登录用户提交本期报名，注意检查本期是否已报名
func (t *ReadChaincode) enrollByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	jsonStr := args[1]
	fmt.Println(jsonStr)
	return shim.Success([]byte(jsonStr))
}

// 查询当前期数已报名用户列表
func (t *ReadChaincode) enrollListByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 插入文章记录，注意校验插入记录是否重复（同一时期内只允许一条文章记录）
func (t *ReadChaincode) insertArticle(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 更新文章记录
func (t *ReadChaincode) updateArticle(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 查询当前登录人对应期数的文章
func (t *ReadChaincode) queryMyArticleByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 获取对应期数的文章列表
func (t *ReadChaincode) listArticleByPeriod(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 评论某篇文章笔记，注意是否评论判断
func (t *ReadChaincode) commentByArticleId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 获取某篇笔记的评论集
func (t *ReadChaincode) listCommentByArticleId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

// 获取指定期数，评论人一共评论的文章笔记数量
func (t *ReadChaincode) getCommentCountByUserId(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	return shim.Success(nil)
}

func main() {
	err := shim.Start(new(ReadChaincode))
	if err != nil {
		fmt.Printf("Error starting Read chaincode %s", err)
	}
}
