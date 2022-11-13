Feature: Receive and send messages

  Scenario: Receive and send a message
    Given the following messages exist in the queue
      | uuid                                 | data         |
      | e9914069-5e95-4b88-a017-70f792dc7d03 | test message |
    When the application processes the messages
    Then the following messages exist in the 'example_sender' queue
      | uuid                                 | data         |
      | e9914069-5e95-4b88-a017-70f792dc7d03 | Sent Message |
    And the application will log the following messages:
      | level | message                                                         |
      | INFO  | Received message with UUID e9914069-5e95-4b88-a017-70f792dc7d03 |
      | INFO  | Sending message with UUID e9914069-5e95-4b88-a017-70f792dc7d03  |

  Scenario: Receive and send a message when no queue exists
    Given the following messages exist in the queue
      | uuid                                 | data         |
      | 60e79e1e-317e-4a05-8b04-0449f325b163 | test message |
    And the 'example_sender' exchange does not exist
    When the application processes the messages
    Then the following messages exist in the 'example_receiver_error' queue
      | uuid                                 | data         |
      | 60e79e1e-317e-4a05-8b04-0449f325b163 | test message |
    And the application will log the following messages:
      | level | message                                                         |
      | INFO  | Received message with UUID 60e79e1e-317e-4a05-8b04-0449f325b163 |
      | INFO  | Sending message with UUID 60e79e1e-317e-4a05-8b04-0449f325b163  |
      | ERROR | Message failed to send                                          |
      | ERROR | Message failed to process                                       |
      | INFO  | Received message with UUID 60e79e1e-317e-4a05-8b04-0449f325b163 |
      | INFO  | Sending message with UUID 60e79e1e-317e-4a05-8b04-0449f325b163  |
      | ERROR | Message failed to send                                          |
      | ERROR | Message failed to process                                       |
      | INFO  | Received message with UUID 60e79e1e-317e-4a05-8b04-0449f325b163 |
      | INFO  | Sending message with UUID 60e79e1e-317e-4a05-8b04-0449f325b163  |
      | ERROR | Message failed to send                                          |
      | ERROR | Message failed to process                                       |
