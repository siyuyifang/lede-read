/*
 *  Copyright 2018, Mindtree Ltd. - All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lede.blockchain.service;

import com.google.common.collect.Maps;
import com.lede.blockchain.constant.Rcode;
import com.lede.blockchain.model.*;
import com.lede.blockchain.model.biz.BizUser;
import com.lede.blockchain.service.biz.BizUserService;
import com.lede.blockchain.util.JsonUtil;
import com.lede.blockchain.util.R;
import com.lede.blockchain.util.TokenUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpStatus;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.fail;

/**
 * class for implementing all the services of com.lede.blockchain such as create channel,
 * recreate channel, install chaincode, instantiate chaincode, invoke chaincode,
 * query chaincode
 */

@Service
@PropertySource("classpath:hyperledger.properties")
public class ChaincodeServiceImpl implements ChaincodeService {

    private static final Logger logger = LoggerFactory.getLogger(ChaincodeServiceImpl.class);
    private static String PATH = System.getProperty("user.dir");
    private static final Config Conf = Config.getConfig();

    @Value("${ADMIN_NAME}")
    private String adminName;

    private String FIXTURES_PATH = PATH + "/src/main/java";

    @Value("${CHAIN_CODE_PATH}")
    private String chainCodePath;

    @Value("${CHAIN_CODE_VERSION}")
    private String chainCodeVersion;

    @Value("${CHANNEL_NAME}")
    private String channelName;

    @Resource
    private BizUserService bizUserService;

    // For setting CryptoSuite only if the application is running for the first time.
    int counter = 0;


    private final ConfigHelper configHelper = new ConfigHelper();
    ChaincodeID chaincodeID;
    private Collection<Org> SampleOrgs;
    HFClient client = HFClient.createNewInstance();

    static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }

    /**
     * takes input as chaincode name and returns chaincode id
     *
     * @param name
     * @return ChaincodeID
     */
    public ChaincodeID getChaincodeId(String name) {
        chaincodeID = ChaincodeID.newBuilder().setName(name).setVersion(chainCodeVersion).setPath(chainCodePath)
                .build();
        return chaincodeID;
    }

    /**
     * checking config at starting
     */
    public void checkConfig() {

        SampleOrgs = Conf.getSampleOrgs();
        if (counter == 0) {
            try {
                client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                counter++;
            } catch (CryptoException | InvalidArgumentException e) {
                // TODO Auto-generated catch block
                logger.error("ChaincodeServiceImpl | checkConfig | " + e.getMessage());
            }
        }

        // Set up hfca for each sample org

        for (Org sampleOrg : SampleOrgs) {
            try {
                sampleOrg.setCAClient(HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
            } catch (MalformedURLException e) {
                logger.error("checkConfig failed!", e);
            }
        }
    }

    private void waitOnFabric(int additional) {
        //NOOP today
    }

    /**
     * For loading user from persistence,if user already exists by taking as
     * input username
     *
     * @param name
     * @return status as string
     */

    public String loadUserFromPersistence(String name) {

        File sampleStoreFile = new File(FIXTURES_PATH + "/HyperledgerEnroll.properties");

        final Store sampleStore = new Store(sampleStoreFile);
        for (Org sampleOrg : SampleOrgs) {

            final String orgName = sampleOrg.getName();
            HyperUser admin = sampleStore.getMember(adminName, orgName);
            // The admin of this org.
            sampleOrg.setAdmin(admin);

            // No need to enroll or register all done in End2endIt !
            HyperUser user = sampleStore.getMember(name, orgName);
            sampleOrg.addUser(user); // Remember user belongs to this Org

            sampleOrg.setPeerAdmin(sampleStore.getMember(orgName + "Admin", orgName));

            final String sampleOrgName = sampleOrg.getName();
            final String sampleOrgDomainName = sampleOrg.getDomainName();

            HyperUser peerOrgAdmin;

            try {
                peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName,
                        sampleOrg.getMSPID(),
                        ConnectionUtil.findFileSk(Paths.get(Conf.getChannelPath(),
                                "crypto-config/peerOrganizations/", sampleOrgDomainName,
                                format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile()),
                        Paths.get(Conf.getChannelPath(), "crypto-config/peerOrganizations/",
                                sampleOrgDomainName, format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
                                        sampleOrgDomainName, sampleOrgDomainName))
                                .toFile());
                sampleOrg.setPeerAdmin(peerOrgAdmin);
            } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException e) {
                logger.error("loadUserFromPersistence failed!", e);
                return "failed";
            }

        }
        return "success";

    }

    /**
     * Reconstruct the channel returns channel that has been reconstructed
     */
    public Channel reconstructChannel() {

        checkConfig();
        try {
            Org sampleOrg = Conf.getSampleOrg("peerOrg1");

            client.setUserContext(sampleOrg.getPeerAdmin());
            Channel newChannel = client.newChannel(channelName);

            for (String orderName : sampleOrg.getOrdererNames()) {

                newChannel.addOrderer(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
                        Conf.getOrdererProperties(orderName)));
            }

            for (String peerName : sampleOrg.getPeerNames()) {
                logger.debug(peerName);
                String peerLocation = sampleOrg.getPeerLocation(peerName);
                Peer peer = client.newPeer(peerName, peerLocation, Conf.getPeerProperties(peerName));

                // Query the actual peer for which channels it belongs to and check
                // it belongs to this channel
                Set<String> channels = client.queryChannels(peer);
                if (!channels.contains(channelName)) {
                    logger.info("Peer %s does not appear to belong to channel %s", peerName, channelName);
                }

                newChannel.addPeer(peer);
                sampleOrg.addPeer(peer);
            }

            for (String eventHubName : sampleOrg.getEventHubNames()) {
                EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
                        Conf.getEventHubProperties(eventHubName));
                newChannel.addEventHub(eventHub);
            }

            newChannel.initialize();

            return newChannel;
        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | reconstructChannel ");
            return null;
        }
    }

    /**
     * enroll and register user at starting takes username as input returns
     * status as string
     */
    public String enrollAndRegister(String uname) {

        // TODO Auto-generated method stub
        try {
            checkConfig();

            File sampleStoreFile = new File(FIXTURES_PATH + "/HyperledgerEnroll.properties");

            final Store sampleStore = new Store(sampleStoreFile);
            for (Org sampleOrg : SampleOrgs) {

                final String orgName = sampleOrg.getName();
                final String mspid = sampleOrg.getMSPID();
                HFCAClient ca = sampleOrg.getCAClient();
                ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                HyperUser admin = sampleStore.getMember(adminName, orgName);
                if (!admin.isEnrolled()) { // Preregistered admin only needs to
                    // be enrolled with Fabric caClient.
                    admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
                    admin.setMspId(mspid);
                }

                sampleOrg.setAdmin(admin); // The admin of this org --

                if (sampleStore.hasMember(uname, sampleOrg.getName())) {
                    String result = loadUserFromPersistence(uname);
                    return result;
                }
                HyperUser user = sampleStore.getMember(uname, sampleOrg.getName());

                if (!user.isRegistered()) { // users need to be registered AND
                    // enrolled
                    RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");


                    user.setEnrollmentSecret(ca.register(rr, admin));


                }
                if (!user.isEnrolled()) {
                    user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
                    user.setMspId(mspid);
                }
                sampleOrg.addUser(user); // Remember user belongs to this Org

                final String sampleOrgName = sampleOrg.getName();
                final String sampleOrgDomainName = sampleOrg.getDomainName();


                HyperUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName,
                        sampleOrg.getMSPID(),
                        ConnectionUtil.findFileSk(Paths.get(Conf.getChannelPath(),
                                "crypto-config/peerOrganizations/", sampleOrgDomainName,
                                format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile()),
                        Paths.get(Conf.getChannelPath(), "crypto-config/peerOrganizations/",
                                sampleOrgDomainName, format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
                                        sampleOrgDomainName, sampleOrgDomainName))
                                .toFile());

                sampleOrg.setPeerAdmin(peerOrgAdmin);

                return "User " + user.getName() + " Enrolled Successfuly";

            }

        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | enrollAndRegister | " + e.getMessage());
            return "Failed to enroll user";

        }
        return "Something went wrong";
    }

    /**
     * Construct the channel returns channel that has been constructed
     */
    public String constructChannel() {
        checkConfig();
        try {

            Org sampleOrg = Conf.getSampleOrg("peerOrg1");

            logger.info("Constructing channel %s", channelName);

            // Only peer Admin org
            client.setUserContext(sampleOrg.getPeerAdmin());

            Collection<Orderer> orderers = new LinkedList<>();

            for (String orderName : sampleOrg.getOrdererNames()) {

                Properties ordererProperties = Conf.getOrdererProperties(orderName);

                // example of setting keepAlive to avoid timeouts on inactive http2
                // connections.
                // Under 5 minutes would require changes to server side to accept
                // faster ping rates.
                // ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime",
                // new Object[] {5L, TimeUnit.MINUTES});
                // ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout",
                // new Object[] {8L, TimeUnit.SECONDS});

                orderers.add(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName), ordererProperties));
            }

            // Just pick the first orderer in the list to create the channel.

            Orderer anOrderer = orderers.iterator().next();
            orderers.remove(anOrderer);

            ChannelConfiguration channelConfiguration = new ChannelConfiguration(
                    new File(PATH + "/artifacts/channel/" + channelName + ".tx"));

            // Create channel that has only one signer that is this orgs peer admin.
            // If channel creation policy needed more signature they would need to
            // be added too.

            Channel newChannel = client.newChannel(channelName, anOrderer, channelConfiguration,
                    client.getChannelConfigurationSignature(channelConfiguration, sampleOrg.getPeerAdmin()));


            logger.info("Created channel %s", channelName);

            for (String peerName : sampleOrg.getPeerNames()) {
                String peerLocation = sampleOrg.getPeerLocation(peerName);

                Properties peerProperties = Conf.getPeerProperties(peerName);
                // properties
                // for
                // peer..
                // if
                // any.
                if (peerProperties == null) {
                    peerProperties = new Properties();
                }
                // Example of setting specific options on grpc's NettyChannelBuilder
                // peerProperties.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize",
                // 9000000);

                Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
                newChannel.joinPeer(peer);
                logger.info("Peer %s joined channel %s", peerName, channelName);
                sampleOrg.addPeer(peer);
            }

            for (Orderer orderer : orderers) { // add remaining orderers if any.
                newChannel.addOrderer(orderer);
            }

            for (String eventHubName : sampleOrg.getEventHubNames()) {

                final Properties eventHubProperties = Conf.getEventHubProperties(eventHubName);
                EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
                        eventHubProperties);
                newChannel.addEventHub(eventHub);
            }

            newChannel.initialize();

            logger.info("Finished initialization channel %s", channelName);

            return "Channel created successfully";
        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | createChannel |" + e.getMessage());
            return "Something went wrong";
        }

    }

    Collection<ProposalResponse> responses;
    Collection<ProposalResponse> successful = new LinkedList<>();
    Collection<ProposalResponse> failed = new LinkedList<>();

    /**
     * installs the chaincode takes as input chaincode name returns status as
     * string
     */
    public String installChaincode(String chaincodeName) {

        try {
            checkConfig();

            chaincodeID = getChaincodeId(chaincodeName);
            Org sampleOrg = Conf.getSampleOrg("peerOrg1");
            Channel channel = reconstructChannel();
            final String channelName = channel.getName();
            boolean isFooChain = channelName.equals(channelName);
            logger.info("Running channel %s", channelName);
            Collection<Peer> channelPeers = channel.getPeers();
            Collection<Orderer> orderers = channel.getOrderers();

            client.setUserContext(sampleOrg.getPeerAdmin());
            logger.info("Creating install proposal");
            InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
            installProposalRequest.setChaincodeID(chaincodeID);
            installProposalRequest.setChaincodeSourceLocation(new File(PATH + "/artifacts/"));
            installProposalRequest.setChaincodeVersion(chainCodeVersion);
            logger.info("Sending install proposal");
            int numInstallProposal = 0;

            Set<Peer> peersFromOrg = sampleOrg.getPeers();
            numInstallProposal = numInstallProposal + peersFromOrg.size();
            responses = client.sendInstallProposal(installProposalRequest, peersFromOrg);
            for (ProposalResponse response : responses) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    out("Successful install proposal response Txid: %s from peer %s", response.getTransactionID(),
                            response.getPeer().getName());
                    successful.add(response);
                } else {
                    failed.add(response);
                }
            }
            SDKUtils.getProposalConsistencySets(responses);
            // }
            logger.info("Received %d install proposal responses. Successful+verified: %d . Failed: %d",
                    numInstallProposal, successful.size(), failed.size());

            if (failed.size() > 0) {
                ProposalResponse first = failed.iterator().next();
                fail("Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
                return "Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage();
            }

            return "Chaincode installed successfully";

        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | installChaincode | " + e.getMessage());
            return "Chaincode installation failed";
        }

    }

    /**
     * instantiates the chaincode takes input chaincode name, function and args
     * returns status as string
     */
    public String instantiateChaincode(String chaincodeName, String chaincodeFunction, String[] chaincodeArgs) {
        try {
            checkConfig();

            chaincodeID = getChaincodeId(chaincodeName);
            Channel channel = reconstructChannel();
            Collection<Orderer> orderers = channel.getOrderers();
            InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
            instantiateProposalRequest.setProposalWaitTime(Conf.getProposalWaitTime());
            instantiateProposalRequest.setChaincodeID(chaincodeID);
            instantiateProposalRequest.setFcn(chaincodeFunction);
            instantiateProposalRequest.setArgs(chaincodeArgs);

            Map<String, byte[]> tm = new HashMap<>();
            tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
            tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
            instantiateProposalRequest.setTransientMap(tm);

            System.out.println("chaincodeEndorsementPolicy path=" + PATH + "/artifacts/chaincodeendorsementpolicy.yaml");
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy
                    .fromYamlFile(new File(PATH + "/artifacts/chaincodeendorsementpolicy.yaml"));

            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

            logger.info(
                    "Sending instantiateProposalRequest to all peers with arguments: a and b set to 100 and %s respectively",
                    "" + (200));

            successful.clear();
            failed.clear();

            responses = channel.sendInstantiationProposal(instantiateProposalRequest);
            for (ProposalResponse response : responses) {
                if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    successful.add(response);
                    logger.info("Succesful instantiate proposal response Txid: %s from peer %s",
                            response.getTransactionID(), response.getPeer().getName());
                } else {
                    failed.add(response);
                }
            }
            logger.info("Received %d instantiate proposal responses. Successful+verified: %d . Failed: %d",
                    responses.size(), successful.size(), failed.size());
            if (failed.size() > 0) {
                ProposalResponse first = failed.iterator().next();

                return "Chaincode instantiation failed , reason Not enough endorsers for instantiate :" + successful.size() + " endorser failed with "
                        + first.getMessage() + ". Was verified:" + first.isVerified();
            }

            ///////////////
            /// Send instantiate transaction to orderer
            logger.info("Sending instantiateTransaction to orderer with a and b set to 100 and %s respectively",
                    "" + (200));
            logger.info("orderers", orderers);
            channel.sendTransaction(successful, orderers).thenApply(transactionEvent -> {

                waitOnFabric(0);

                // must be valid to be here.
                logger.info("transaction event is valid", transactionEvent.isValid());
                logger.info("Finished instantiate transaction with transaction id %s",
                        transactionEvent.getTransactionID());

                return null;
            }).exceptionally(e -> {
                if (e instanceof TransactionEventException) {
                    BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
                    if (te != null) {
                        fail(format("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage()));
                        logger.info("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage());
                    }
                }

                logger.info(" failed with %s exception %s", e.getClass().getName(), e.getMessage());
                return null;
            }).get(Conf.getTransactionWaitTime(), TimeUnit.SECONDS);

            return "Chaincode instantiated Successfully";

        } catch (Exception e) {

            logger.error("ChaincodeServiceImpl | instantiateChaincode |" + e.getMessage());
            return "Chaincode instantiation failed , reason " + e.getMessage();

        }

    }

    /**
     * invokes the chaincode takes input chaincode name, function and args
     * returns status as string
     */
    public String invokeChaincode(String chaincodename, String chaincodeFunction, String[] chaincodeArgs) {

        try {
            checkConfig();

            chaincodeID = getChaincodeId(chaincodename);
            Org sampleOrg = Conf.getSampleOrg("peerOrg1");
            Channel channel = reconstructChannel();
            successful.clear();
            failed.clear();

            ///////////////
            /// Send transaction proposal to all peers
            logger.debug("chaincodeFunction" + chaincodeFunction);
            logger.debug("chaincodeArgs" + chaincodeArgs[0] + chaincodeArgs[1] + chaincodeArgs[2]);
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chaincodeID);
            transactionProposalRequest.setFcn(chaincodeFunction);
            transactionProposalRequest.setProposalWaitTime(Conf.getProposalWaitTime());
            transactionProposalRequest.setArgs(chaincodeArgs);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            tm2.put("result", ":)".getBytes(UTF_8)); /// This should be returned

            transactionProposalRequest.setTransientMap(tm2);

            logger.info("sending transactionProposal to all peers with arguments: move(a,b,100)");

            Collection<ProposalResponse> transactionPropResp = channel
                    .sendTransactionProposal(transactionProposalRequest, channel.getPeers());
            for (ProposalResponse response : transactionPropResp) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    logger.info("Successful transaction proposal response Txid: %s from peer %s",
                            response.getTransactionID(), response.getPeer().getName());
                    successful.add(response);
                } else {
                    failed.add(response);
                }
            }
            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
                    .getProposalConsistencySets(transactionPropResp);
            if (proposalConsistencySets.size() != 1) {
                logger.info(format("Expected only one set of consistent proposal responses but got %d",
                        proposalConsistencySets.size()));
            }

            logger.info("Received %d transaction proposal responses. Successful+verified: %d . Failed: %d",
                    transactionPropResp.size(), successful.size(), failed.size());
            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
                logger.info("Not enough endorsers for invoke(move a,b,100):" + failed.size() + " endorser error: "
                        + firstTransactionProposalResponse.getMessage() + ". Was verified: "
                        + firstTransactionProposalResponse.isVerified());
            }
            logger.info("Successfully received transaction proposal responses.");
            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChaincodeActionResponsePayload(); // This is the
            // data
            // returned
            // by the
            // chaincode.
            String resultAsString = null;
            if (x != null) {
                resultAsString = new String(x, "UTF-8");
            }


            logger.debug("getChaincodeActionResponseReadWriteSetInfo:::"
                    + resp.getChaincodeActionResponseReadWriteSetInfo());
            ChaincodeID cid = resp.getChaincodeID();

            ////////////////////////////
            // Send Transaction Transaction to orderer
            logger.info("Sending chaincode transaction(move a,b,100) to orderer.");
            channel.sendTransaction(successful).thenApply(transactionEvent -> {

                waitOnFabric(0);

                logger.info("transaction event is valid", transactionEvent.isValid()); // must
                // be
                // valid
                // to
                // be
                // here.
                logger.info("Finished invoke transaction with transaction id %s", transactionEvent.getTransactionID());

                return "Chaincode invoked successfully " + transactionEvent.getTransactionID();
            }).exceptionally(e -> {
                if (e instanceof TransactionEventException) {
                    BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
                    if (te != null) {
                        fail(format("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage()));
                        logger.info("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage());
                    }
                }

                logger.info("failed with %s exception %s", e.getClass().getName(), e.getMessage());
                return "Error";
            }).get(Conf.getTransactionWaitTime(), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.info("Caught an exception while invoking chaincode");
            logger.error("ChaincodeServiceImpl | invokeChaincode | " + e.getMessage());
            return "Caught an exception while invoking chaincode";

        }
        return "Transaction invoked successfully";
    }

    /**
     * queries the chaincode takes input chaincode name, function and args
     * returns payload from com.lede.blockchain as string
     */
    public String queryChaincode(String name, String chaincodeFunction, String[] chaincodeArgs) {
        try {
            checkConfig();

            chaincodeID = getChaincodeId(name);
            Channel channel = reconstructChannel();
            logger.debug("Now query chaincode for the value. chaincodeArgs = {} ", JsonUtil.toJson(chaincodeArgs));
            logger.debug(chaincodeFunction);
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();


            queryByChaincodeRequest.setArgs(chaincodeArgs);
            queryByChaincodeRequest.setFcn(chaincodeFunction);
            queryByChaincodeRequest.setChaincodeID(chaincodeID);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
            queryByChaincodeRequest.setTransientMap(tm2);


            Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest,
                    channel.getPeers());

            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    logger.debug("Failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: "
                            + proposalResponse.getStatus() + ". Messages: " + proposalResponse.getMessage()
                            + ". Was verified : " + proposalResponse.isVerified());
                } else {

                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();

                    return payload;
                }

            }

        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | queryChaincode | " + e.getMessage());
        }
        return "Caught an exception while quering chaincode";
    }

    /**
     * gives com.lede.blockchain info
     */
    @Override
    public void blockchainInfo(Org sampleOrg, Channel channel) {

        try {
            checkConfig();

            String channelName = channel.getName();
            Set<Peer> peerSet = sampleOrg.getPeers();
            // Peer queryPeer = peerSet.iterator().next();
            // out("Using peer %s for channel queries", queryPeer.getName());

            BlockchainInfo channelInfo = channel.queryBlockchainInfo();
            logger.info("Channel info for : " + channelName);
            logger.info("Channel height: " + channelInfo.getHeight());

            String chainCurrentHash = Hex.encodeHexString(channelInfo.getCurrentBlockHash());
            String chainPreviousHash = Hex.encodeHexString(channelInfo.getPreviousBlockHash());
            logger.info("Chain current block hash: " + chainCurrentHash);
            logger.info("Chainl previous block hash: " + chainPreviousHash);

            // Query by block number. Should return latest block, i.e. block
            // number 2
            BlockInfo returnedBlock = channel.queryBlockByNumber(channelInfo.getHeight() - 1);
            String previousHash = Hex.encodeHexString(returnedBlock.getPreviousHash());
            logger.info("queryBlockByNumber returned correct block with blockNumber " + returnedBlock.getBlockNumber()
                    + " \n previous_hash " + previousHash);

            // Query by block hash. Using latest block's previous hash so should
            // return block number 1
            byte[] hashQuery = returnedBlock.getPreviousHash();
            returnedBlock = channel.queryBlockByHash(hashQuery);
            logger.info("queryBlockByHash returned block with blockNumber " + returnedBlock.getBlockNumber());

        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | blockchainInfo | " + e.getMessage());
        }
    }

    /**
     * invokes the chaincode takes input chaincode name, function and args
     * returns status as string
     */
    @Override
    public Map<String, Object> invokeBizChaincode(String chaincodename, String chaincodeFunction, String[] chaincodeArgs) {

        Map<String, Object> result = Maps.newHashMap();
        try {
            checkConfig();

            chaincodeID = getChaincodeId(chaincodename);
            //Org sampleOrg = Conf.getSampleOrg("peerOrg1");
            Channel channel = reconstructChannel();
            successful.clear();
            failed.clear();

            /// Send transaction proposal to all peers
            logger.debug("chaincodeFunction" + chaincodeFunction);
            logger.debug("chaincodeArgs, {}", JsonUtil.toJson(chaincodeArgs));
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chaincodeID);
            transactionProposalRequest.setFcn(chaincodeFunction);
            transactionProposalRequest.setProposalWaitTime(Conf.getProposalWaitTime());
            transactionProposalRequest.setArgs(chaincodeArgs);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
            // This should be returned
            tm2.put("result", ":)".getBytes(UTF_8));
            transactionProposalRequest.setTransientMap(tm2);

            logger.info("sending transactionProposal to all peers with arguments");

            Collection<ProposalResponse> transactionPropResp = channel
                    .sendTransactionProposal(transactionProposalRequest, channel.getPeers());
            for (ProposalResponse response : transactionPropResp) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    logger.info("Successful transaction proposal response Txid: {} from peer {}",
                            response.getTransactionID(), response.getPeer().getName());
                    successful.add(response);
                } else {
                    failed.add(response);
                }
            }
            Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
                    .getProposalConsistencySets(transactionPropResp);
            if (proposalConsistencySets.size() != 1) {
                logger.info(format("Expected only one set of consistent proposal responses but got {}",
                        proposalConsistencySets.size()));
            }

            logger.info("Received %d transaction proposal responses. Successful+verified: {} . Failed: {}",
                    transactionPropResp.size(), successful.size(), failed.size());
            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
                logger.info("Not enough endorsers for invoke:" + failed.size() + " endorser error: "
                        + firstTransactionProposalResponse.getMessage() + ". Was verified: "
                        + firstTransactionProposalResponse.isVerified());
            }
            logger.info("Successfully received transaction proposal responses.");
            ProposalResponse resp = transactionPropResp.iterator().next();
            logger.debug("getChaincodeActionResponseReadWriteSetInfo:::" + resp.getChaincodeActionResponseReadWriteSetInfo());

            //根据返回状态码判断是否正常完成
            int status = resp.getChaincodeActionResponseStatus();
            if (status != 200) {
                logger.info("response status = {}, message = {}", status, resp.getMessage());
                return R.error(status, resp.getMessage());
            }

//            byte[] x = resp.getChaincodeActionResponsePayload();
//            String resultAsString;
//            if (x != null) {
//                resultAsString = new String(x, "UTF-8");
//                // 根据返回值确认是否已成功执行。code非200则直接返回
//                Map<String, Object> resultMap = JsonUtil.parseJSON2Map(resultAsString);
//                if (resultMap != null && !"200".equals(String.valueOf(resultMap.get("code")))) {
//                    return resultMap;
//                }
//            }

            /**
             * Send Transaction Transaction to orderer
             */
            logger.info("Sending chaincode transaction to orderer.");
            channel.sendTransaction(successful).thenApply(transactionEvent -> {

                waitOnFabric(0);
                logger.info("transaction event is valid", transactionEvent.isValid());
                logger.info("Finished invoke transaction with transaction id {}", transactionEvent.getTransactionID());

                result.put("code", HttpStatus.SC_OK);
                result.put("msg", "Chaincode invoked successfully " + transactionEvent.getTransactionID());
                return result;
            }).exceptionally(e -> {
                if (e instanceof TransactionEventException) {
                    BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
                    if (te != null) {
                        fail(format("Transaction with txid {} failed. {}", te.getTransactionID(), e.getMessage()));
                        logger.info("Transaction with txid {} failed. {}", te.getTransactionID(), e.getMessage());
                    }
                }

                logger.info("failed with {} exception {}", e.getClass().getName(), e.getMessage());
                result.put("code", Rcode.BIZ_FAILED.getCode());
                result.put("msg", Rcode.BIZ_FAILED.getMsg());
                return result;
            }).get(Conf.getTransactionWaitTime(), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | invokeChaincode | ", e);
            result.put("code", Rcode.BIZ_ERROR.getCode());
            result.put("msg", "Caught an exception while invoking chaincode");
            return result;

        }
        result.put("code", HttpStatus.SC_OK);
        result.put("msg", "Transaction invoked successfully");
        return result;
    }


    /**
     * queries the chaincode takes input chaincode name, function and args
     * returns payload from com.lede.blockchain as string
     */
    @Override
    public Map<String, Object> queryBizChaincode(String name, String chaincodeFunction, String[] chaincodeArgs) {
        try {
            checkConfig();

            chaincodeID = getChaincodeId(name);
            Channel channel = reconstructChannel();
            logger.debug("Now query chaincode for the value. chaincodeArgs = {} ", JsonUtil.toJson(chaincodeArgs));
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();

            queryByChaincodeRequest.setArgs(chaincodeArgs);
            queryByChaincodeRequest.setFcn(chaincodeFunction);
            queryByChaincodeRequest.setChaincodeID(chaincodeID);

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
            queryByChaincodeRequest.setTransientMap(tm2);

            Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest,
                    channel.getPeers());

            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    logger.info("Failed query proposal from peer {}, | status: {}, | message:{}, | Was verified: {}", proposalResponse.getPeer().getName(),
                            proposalResponse.getStatus(), proposalResponse.getMessage(), proposalResponse.isVerified());
                } else {

                    int status = proposalResponse.getChaincodeActionResponseStatus();
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();

                    logger.info("proposalResponse status = {} , payload = {}", status, payload);
                    if (status == HttpStatus.SC_OK) {
                        return R.ok().put("data", payload);
                    } else {
                        return R.error(status, payload);
                    }
                }

            }

        } catch (Exception e) {
            logger.error("ChaincodeServiceImpl | queryBizChaincode | {}", e.getMessage());
        }

        return R.error(Rcode.BIZ_FAILED.getCode(), Rcode.BIZ_FAILED.getMsg());
    }

    /**
     * enroll and register user at starting takes username as input returns
     * status as string
     */
    public Map<String, Object> enrollAndRegisterBiz(String cellphone, String username, String password) {

        try {

            //配置信息检查
            checkConfig();

            File sampleStoreFile = new File(FIXTURES_PATH + "/HyperledgerEnroll.properties");

            final Store sampleStore = new Store(sampleStoreFile);
            for (Org sampleOrg : SampleOrgs) {

                HFCAClient ca = sampleOrg.getCAClient();
                final String orgName = sampleOrg.getName();
                final String mspid = sampleOrg.getMSPID();
                ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
                HyperUser admin = sampleStore.getMember(adminName, orgName);
                if (!admin.isEnrolled()) {
                    admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
                    admin.setMspId(mspid);
                }

                sampleOrg.setAdmin(admin);

                if (sampleStore.hasMember(username, sampleOrg.getName())) {
                    String flag = loadUserFromPersistence(username);
                    if ("success".equals(flag)) {
                        return R.other(Rcode.BIZ_REPEAT);
                    }
                    return R.other(Rcode.BIZ_FAILED);
                }
                HyperUser user = sampleStore.getMember(username, sampleOrg.getName());

                if (!user.isRegistered()) {
                    // users need to be registered AND enrolled
                    RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");

                    user.setEnrollmentSecret(ca.register(rr, admin));

                }
                if (!user.isEnrolled()) {
                    user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
                    user.setMspId(mspid);
                }
                // Remember user belongs to this Org
                sampleOrg.addUser(user);

                final String sampleOrgName = sampleOrg.getName();
                final String sampleOrgDomainName = sampleOrg.getDomainName();

                HyperUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName,
                        sampleOrg.getMSPID(),
                        ConnectionUtil.findFileSk(Paths.get(Conf.getChannelPath(),
                                "crypto-config/peerOrganizations/", sampleOrgDomainName,
                                format("/users/Admin@%s/msp/keystore", sampleOrgDomainName)).toFile()),
                        Paths.get(Conf.getChannelPath(), "crypto-config/peerOrganizations/",
                                sampleOrgDomainName, format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
                                        sampleOrgDomainName, sampleOrgDomainName))
                                .toFile());

                sampleOrg.setPeerAdmin(peerOrgAdmin);

                //保存用户注册信息,原则上不会有重复用户
                BizUser bizUser = new BizUser(cellphone, password, username, 1, Clock.systemDefaultZone().millis());
                bizUserService.saveUser(bizUser);
                logger.info("User {} Enrolled Successfully", username);
                String token = TokenUtil.getToken(cellphone);
                return R.ok().put("token", token);

            }

        } catch (Exception e) {
            logger.error("enrollAndRegister failed!", e);
            return R.other(Rcode.BIZ_FAILED);

        }
        return R.other(Rcode.BIZ_ERROR);
    }
}
